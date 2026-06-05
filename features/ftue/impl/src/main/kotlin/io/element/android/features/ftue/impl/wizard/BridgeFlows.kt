/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 */

package io.prism.android.features.ftue.impl.wizard

import androidx.compose.ui.text.input.KeyboardType
import io.prism.android.libraries.matrix.api.PRISMClient
import io.prism.android.libraries.matrix.api.core.RoomId
import io.prism.android.libraries.matrix.api.createroom.CreateRoomParameters
import io.prism.android.libraries.matrix.api.createroom.RoomPreset
import io.prism.android.libraries.matrix.api.roomdirectory.RoomVisibility
import kotlinx.coroutines.flow.first
import timber.log.Timber

/**
 * Bridge command vocabularies — concrete [BridgeFlow] implementations.
 *
 * Each `mautrix-*` bridge has its own slightly different bot grammar; we
 * encode the bits PRISM v1.0.0 cares about here. The patterns below are
 * derived from the upstream docs at https://docs.mau.fi/bridges/.
 *
 * Heuristics for success / failure detection are intentionally conservative
 * (case-insensitive substring matches against the most stable phrases the
 * bots emit) so that a future bridge upgrade does not silently break the
 * onboarding flow.
 */

internal class WhatsAppBridgeFlow : BridgeFlow {
    override val displayName = "WhatsApp"
    override val botUserId = "@pwb-bot:matrix.fathertkt.uk"

    // Empty initialCommand + non-null initialPrompt = collect phone number FIRST
    // (so users with only one phone don't have to scan a QR they're displaying on
    // the same device). The actual `login phone <number>` command is built in
    // followUpCommand() after the user submits the number.
    override val initialCommand = ""
    override val promptDescription =
        "Enter your phone number in international format with country code " +
            "(örn. +1 555 123 4567 ABD için, +44 20 7946 0958 İngiltere için, +90 555 111 22 33 Türkiye için). " +
            "WhatsApp uygulamasında Ayarlar → Bağlı cihazlar → Cihaz bağla → Telefon numarası ile bağla " +
            "menüsünde gireceğin 8 haneli pairing kodunu üreteceğiz."
    override val initialPrompt = FlowDecision.AskForInput(
        prompt = promptDescription,
        inputLabel = "Phone number",
        inputPlaceholder = "+15551234567",
        // Pre-fill the leading "+" so the user can type only digits on a phone keypad
        // (most Android numeric keyboards don't expose "+" without long-press / symbol toggle).
        initialValue = "+",
        keyboardType = KeyboardType.Phone,
    )

    override fun followUpCommand(input: String): String {
        // mautrix-whatsapp REQUIRES E.164 with leading "+" and no separators.
        // Be lenient about user formatting:
        //  * strip everything except digits and a leading + (spaces, dashes, parens, dots)
        //  * if the user wrote "00<cc>..." (common international prefix), turn it into "+<cc>..."
        //  * always prepend "+" if missing — most onboarding-stage users forget it
        val trimmed = input.trim()
        val digitsOnly = trimmed.filter { it.isDigit() }
        val normalized = when {
            digitsOnly.isEmpty() -> ""
            trimmed.startsWith("+") -> "+$digitsOnly"
            digitsOnly.startsWith("00") -> "+${digitsOnly.removePrefix("00")}"
            else -> "+$digitsOnly"
        }
        return "login phone $normalized"
    }

    /**
     * Extract a pairing code (6 or 8 alphanumeric characters, with or without a hyphen/space
     * in the middle) from the bot's reply. We anchor on the literal "code" word so unrelated
     * tokens like "Pairing" or other 6+ letter words don't accidentally match.
     *
     * mautrix-whatsapp has emitted both 8-char (ABCD-WXYZ) and 6-char (ABC-DEF or 123456)
     * codes across versions — be permissive about both.
     */
    private fun extractPairingCode(body: String): String? {
        val trimmed = body.trim()

        // Case 1: bot sometimes sends the code by itself as a follow-up message
        // (e.g. just "ABCD-EFGH" or "ABCDEFGH" with no surrounding prose). If the body
        // is short enough, validate it directly without any anchor.
        if (trimmed.length <= 16) {
            val cleaned = trimmed.filter { it.isLetterOrDigit() }
            if (looksLikePairingCode(cleaned)) return formatPairingCode(cleaned)
        }

        // Case 2: long body with a "code" label — search the trailing region for a token
        // that passes validation. We use findAll (not find) so common false matches like
        // "WhatsApp" or "logging-in" sitting between the label and the real code don't
        // shadow the actual answer.
        val anchorIdx = body.lowercase().lastIndexOf("code")
        if (anchorIdx < 0) return null
        val tail = body.substring(anchorIdx + 4).take(120)
        val tokenRegex = Regex("""([A-Za-z0-9]{3,4})[\s\-]?([A-Za-z0-9]{3,4})""")
        for (m in tokenRegex.findAll(tail)) {
            val combined = m.groupValues[1] + m.groupValues[2]
            if (looksLikePairingCode(combined)) return formatPairingCode(combined)
        }
        return null
    }

    /** Pairing codes are A–Z + 0–9, never mixed-case English words. Reject prose. */
    private fun looksLikePairingCode(s: String): Boolean {
        if (s.length !in 6..8) return false
        val upper = s.uppercase()
        val hasDigit = upper.any { it.isDigit() }
        val allAlnum = upper.all { it.isDigit() || it.isUpperCase() }
        return hasDigit || allAlnum
    }

    private fun formatPairingCode(s: String): String {
        val mid = s.length / 2
        return s.substring(0, mid).uppercase() + "-" + s.substring(mid).uppercase()
    }

    override fun classify(event: BotEvent): FlowDecision {
        return when (event) {
            // We're not using QR anymore; if the bridge ever sends one we just ignore it.
            is BotEvent.Image -> FlowDecision.Ignore
            is BotEvent.Text -> {
                val body = event.body
                val bodyLc = body.lowercase()

                // PRIMARY path: mautrix-whatsapp puts the actual pairing code inside the
                // structured fi.mau.bridge.next_step field. The body itself is just the
                // human-readable instruction "Input the pairing code in the WhatsApp mobile
                // app to log in" with no code in it.
                event.nextStep?.let { ns ->
                    if (ns.stepId == "fi.mau.whatsapp.login.code" && !ns.data.isNullOrEmpty()) {
                        return FlowDecision.ShowPairingCode(
                            code = ns.data.uppercase(),
                            caption = "Open WhatsApp: Settings → Linked Devices → " +
                                "Link a Device → Link with phone number. Enter the code above.",
                        )
                    }
                }

                // Legacy fallback: older mautrix-whatsapp builds (and other bridges) embed
                // the code directly inside the message body. Try to find it that way too.
                if ("code" in bodyLc) {
                    extractPairingCode(body)?.let { code ->
                        return FlowDecision.ShowPairingCode(
                            code = code,
                            caption = "Open WhatsApp: Settings → Linked Devices → " +
                                "Link a Device → Link with phone number. Enter the code above.",
                        )
                    }
                }

                // Standalone pairing code (2nd message without "code" keyword)
                extractPairingCode(body)?.let { code ->
                    return FlowDecision.ShowPairingCode(
                        code = code,
                        caption = "Open WhatsApp: Settings → Linked Devices → " +
                            "Link a Device → Link with phone number. Enter the code above.",
                    )
                }

                when {
                    bodyLc.contains("successfully logged in") ||
                        bodyLc.contains("login successful") ||
                        bodyLc.contains("you are now logged in") ||
                        bodyLc.contains("connected to whatsapp") ||
                        bodyLc.contains("sync complete") ||
                        bodyLc.startsWith("logged in as") -> FlowDecision.Success

                    bodyLc.contains("invalid phone") ||
                        bodyLc.contains("not a valid phone number") ||
                        bodyLc.contains("phone number is not registered") -> FlowDecision.Failure(
                        "Phone number is invalid. Please try again in international format (e.g. +15551234567).",
                    )

                    bodyLc.contains("login timed out") ||
                        bodyLc.contains("pairing code expired") ||
                        bodyLc.contains("login cancelled") -> FlowDecision.Failure(
                        "Pairing code expired. Please try again.",
                    )

                    bodyLc.contains("connecting to whatsapp") ||
                        bodyLc.contains("syncing") -> FlowDecision.Progress(
                        "Connecting to WhatsApp...",
                    )

                    else -> FlowDecision.Ignore
                }
            }
        }
    }

    override suspend fun onSetupComplete(matrixClient: PRISMClient, botRoomId: RoomId) {
        try {
            val spaceName = "WhatsApp"
            val spaces = matrixClient.spaceService.topLevelSpacesFlow.first()
            val existingSpace = spaces.find { it.displayName == spaceName }
            val spaceId = if (existingSpace != null) {
                existingSpace.roomId
            } else {
                val params = CreateRoomParameters(
                    name = spaceName,
                    isEncrypted = true,
                    isDirect = false,
                    visibility = RoomVisibility.Private,
                    preset = RoomPreset.PRIVATE_CHAT,
                    isSpace = true,
                )
                matrixClient.createRoom(params).getOrThrow()
            }
            matrixClient.spaceService.addChildToSpace(spaceId, botRoomId).getOrThrow()
            Timber.d("WhatsAppBridgeFlow: created/added bot room to %s space", spaceName)
        } catch (t: Throwable) {
            Timber.w(t, "WhatsAppBridgeFlow: onSetupComplete failed")
        }
    }
}

/**
 * mautrix-meta supports both Facebook Messenger and Instagram. For PRISM v1.0.0
 * we focus on Instagram (per the project plan). The bridge accepts a cookie-string
 * via `login` followed by a JSON payload — we capture that string from the user
 * and send it along.
 *
 * If/when we add an in-app WebView extractor, the same flow can be reused: the
 * extractor populates the prompt and submits it on behalf of the user.
 */
internal class MetaBridgeFlow : BridgeFlow {
    override val displayName = "Instagram (Meta)"
    override val botUserId = "@pmb-bot:matrix.fathertkt.uk"
    override val initialCommand = "login"
    override val promptDescription =
        "We will open an embedded browser in the next step to connect your Instagram account. " +
            "After logging in, cookies will be captured automatically — no manual copying needed."

    // Cookie shape required by mautrix-meta's instagram login. Pulled from the bridge's
    // own login spec (see prism-meta logs at startup: step_id=fi.mau.meta.cookies).
    private val instagramCookieNames = listOf("sessionid", "csrftoken", "ds_user_id", "mid", "ig_did")
    private val instagramSuccessUrlPattern =
        """^https://www\.instagram\.com/(?:accounts/login/)?(?:direct/(?:inbox/|t/[0-9]+/)?)?(?:\?.*)?$"""
    private val instagramUserAgent =
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) " +
            "Chrome/138.0.0.0 Safari/537.36"

    /**
     * mautrix-meta's `login` command in interactive mode expects the next message
     * from the same room to be a JSON object with the cookie set, e.g.:
     *
     *   {"sessionid":"...","ds_user_id":"...","csrftoken":"...","mid":"...","ig_did":"..."}
     *
     * The WebView wizard step builds this JSON for us; manual fallback also passes
     * verbatim through for power users who paste something themselves.
     */
    override fun followUpCommand(input: String): String = input.trim()

    override fun classify(event: BotEvent): FlowDecision {
        return when (event) {
            is BotEvent.Image -> FlowDecision.Ignore

            is BotEvent.Text -> {
                // PRIMARY: structured next_step from mautrix-meta. The cookie-collection step
                // is identified by step_id. WebView cookie scraping is unreliable on many
                // Android devices (white screen, cookie read failures), so we use manual
                // cookie input which always works.
                event.nextStep?.let { ns ->
                    if (ns.stepId == "fi.mau.meta.cookies") {
                        return manualCookiePrompt()
                    }
                }

                val body = event.body.lowercase()
                when {
                    body.contains("successfully logged in") ||
                        body.contains("login successful") ||
                        body.contains("you are now logged in") -> FlowDecision.Success

                    // Instagram challenge / 2FA paths
                    body.contains("checkpoint required") ||
                        body.contains("two-factor") ||
                        body.contains("login challenge") -> FlowDecision.Failure(
                        "Instagram requires additional verification (2FA / checkpoint). Please " +
                            "verify your account in the browser first, then re-enter the cookies.",
                    )

                    body.contains("invalid credentials") ||
                        body.contains("login failed") ||
                        body.contains("invalid cookie") -> FlowDecision.Failure(
                        "Cookies were not accepted. Please copy the latest cookies and try again.",
                    )

                    // Fallback: bridge asks for cookies in plain text
                    body.contains("enter a json object") ||
                        body.contains("send the cookie") ||
                        body.contains("paste your cookies") ||
                        body.contains("send your login json") ||
                        body.contains("waiting for cookies") -> manualCookiePrompt()

                    body.contains("connecting") ||
                        body.contains("syncing") ||
                        body.contains("logging in") -> FlowDecision.Progress(
                        "Connecting to Instagram...",
                    )

                    else -> FlowDecision.Ignore
                }
            }
        }
    }

    private fun manualCookiePrompt(): FlowDecision.AskForInput {
        return FlowDecision.AskForInput(
            prompt = "You need to enter Instagram cookies manually.\n\n" +
                "1. Log in to instagram.com in Chrome\n" +
                "2. F12 → Application → Cookies → instagram.com\n" +
                "3. Şu çerezleri kopyala: sessionid, csrftoken, ds_user_id, mid, ig_did\n" +
                "4. Aşağıdaki JSON formatında yapıştır:\n" +
                "{\"sessionid\":\"...\",\"csrftoken\":\"...\",\"ds_user_id\":\"...\",\"mid\":\"...\",\"ig_did\":\"...\"}",
            inputLabel = "Cookies (JSON)",
            inputPlaceholder = "{\"sessionid\":\"...\",\"csrftoken\":\"...\",\"ds_user_id\":\"...\",\"mid\":\"...\",\"ig_did\":\"...\"}",
        )
    }

    override suspend fun onSetupComplete(matrixClient: PRISMClient, botRoomId: RoomId) {
        try {
            val spaceName = "Instagram"
            val spaces = matrixClient.spaceService.topLevelSpacesFlow.first()
            val existingSpace = spaces.find { it.displayName == spaceName }
            val spaceId = if (existingSpace != null) {
                existingSpace.roomId
            } else {
                val params = CreateRoomParameters(
                    name = spaceName,
                    isEncrypted = true,
                    isDirect = false,
                    visibility = RoomVisibility.Private,
                    preset = RoomPreset.PRIVATE_CHAT,
                    isSpace = true,
                )
                matrixClient.createRoom(params).getOrThrow()
            }
            matrixClient.spaceService.addChildToSpace(spaceId, botRoomId).getOrThrow()
            Timber.d("MetaBridgeFlow: created/added bot room to %s space", spaceName)
        } catch (t: Throwable) {
            Timber.w(t, "MetaBridgeFlow: onSetupComplete failed")
        }
    }
}
