/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.preferences.impl.bridgesettings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import dev.zacsweers.metro.Inject
import io.prism.android.features.preferences.impl.BuildConfig
import io.prism.android.features.preferences.impl.R
import io.prism.android.libraries.architecture.Presenter
import io.prism.android.services.toolbox.api.strings.StringProvider
import io.prism.android.libraries.matrix.api.PRISMClient
import io.prism.android.libraries.matrix.api.core.RoomId
import io.prism.android.libraries.matrix.api.core.UserId
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import timber.log.Timber

@Inject
class BridgeSettingsPresenter(
    private val matrixClient: PRISMClient,
    private val stringProvider: StringProvider,
) : Presenter<BridgeSettingsState> {

    @Composable
    override fun present(): BridgeSettingsState {
        val coroutineScope = rememberCoroutineScope()
        val interactor = remember { BridgeBotInteractor(matrixClient) }

        var isLoading by remember { mutableStateOf(false) }
        var snackbarMessage by remember { mutableStateOf<String?>(null) }
        var dialog by remember { mutableStateOf<BridgeDialog?>(null) }
        var activeJob by remember { mutableStateOf<Job?>(null) }
        var activeRoomId by remember { mutableStateOf<RoomId?>(null) }
        var activeBotId by remember { mutableStateOf<UserId?>(null) }
        var lastPhoneNumber by remember { mutableStateOf<String?>(null) }
        var forcedStatuses by remember { mutableStateOf<Map<String, Boolean>>(emptyMap()) }
        var bridgeIdentifiers by remember { mutableStateOf<Map<String, String>>(emptyMap()) }

        val summaries by matrixClient.roomListService.allRooms.summaries.collectAsState(initial = emptyList())
        val bridges = remember(summaries, forcedStatuses, bridgeIdentifiers) {
            val whatsappBot = BuildConfig.WHATSAPP_BRIDGE_BOT

            val whatsappDm = summaries.find {
                it.isOneToOne && it.info.heroes.any { h -> h.userId.value == whatsappBot }
            }

            listOf(
                BridgeInfo(
                    platform = "whatsapp",
                    displayName = stringProvider.getString(R.string.screen_bridge_settings_whatsapp_display_name),
                    botUserId = whatsappBot,
                    isActive = forcedStatuses["whatsapp"] ?: (whatsappDm != null),
                    roomId = whatsappDm?.roomId,
                    identifier = bridgeIdentifiers["whatsapp"],
                ),
            ).toImmutableList()
        }

        // Verify active bridges on first composition — stale sessions after account reset show "not logged in"
        LaunchedEffect(Unit) {
            val currentBridges = bridges
            currentBridges.filter { it.isActive }.forEach { bridge ->
                launch {
                    try {
                        val botUserId = UserId(bridge.botUserId)
                        val roomId = withTimeoutOrNull(30_000L) {
                            interactor.openBotRoom(botUserId).getOrThrow()
                        } ?: return@launch
                        interactor.sendCommand(roomId, "status").getOrThrow()
                        var reallyActive = true
                        var stopObserving = false
                        withTimeoutOrNull(15_000L) {
                            interactor.observeBotEvents(
                                roomId = roomId,
                                botUserId = botUserId,
                                skipExisting = true,
                            ) { event ->
                                when (event) {
                                    is BotEvent.Text -> {
                                        val bodyLc = event.body.lowercase()
                                        if (bodyLc.contains("not logged in") ||
                                            bodyLc.contains("not connected") ||
                                            bodyLc.contains("no session") ||
                                            bodyLc.contains("you must log in")
                                        ) {
                                            reallyActive = false
                                        } else {
                                            // Try to extract identifier from status reply
                                            extractIdentifier(event.body, bridge.platform)?.let { id ->
                                                bridgeIdentifiers = bridgeIdentifiers.toMutableMap().apply { put(bridge.platform, id) }
                                            }
                                        }
                                        stopObserving = true
                                    }
                                    else -> { stopObserving = false }
                                }
                                stopObserving
                            }.collect { }
                        }
                        if (!reallyActive) {
                            forcedStatuses = forcedStatuses.toMutableMap().apply { put(bridge.platform, false) }
                            bridgeIdentifiers = bridgeIdentifiers.toMutableMap().apply { remove(bridge.platform) }
                        }
                    } catch (t: Throwable) {
                        if (t is kotlinx.coroutines.CancellationException) throw t
                        Timber.w(t, "BridgeSettings: status check failed for ${bridge.platform}")
                    }
                }
            }
        }

        fun showError(message: String) {
            isLoading = false
            dialog = BridgeDialog.Error(message)
        }

        fun startObservingBotEvents(roomId: RoomId, botUserId: UserId, platform: String) {
            activeJob?.cancel()
            activeJob = coroutineScope.launch {
                try {
                    val completed = withTimeoutOrNull(120_000L) {
                        interactor.observeBotEvents(
                            roomId = roomId,
                            botUserId = botUserId,
                            skipExisting = true,
                        ) { event ->
                            var shouldStop = false
                            when (event) {
                                is BotEvent.Text -> {
                                    val body = event.body
                                    val bodyLc = body.lowercase()
                                    val nextStep = event.nextStep

                                    if (platform == "whatsapp") {
                                        val nsData = nextStep?.data
                                        val hasPairingStep = nextStep?.stepId == "fi.mau.whatsapp.login.code" && !nsData.isNullOrEmpty()
                                        if (hasPairingStep) {
                                            isLoading = false
                                            dialog = BridgeDialog.PairingCode(
                                                platform = platform,
                                                code = nsData.uppercase(),
                                                caption = stringProvider.getString(R.string.screen_bridge_settings_pairing_code_caption),
                                            )
                                            shouldStop = false
                                        } else if ("code" in bodyLc) {
                                            val code = extractPairingCode(body)
                                            if (code != null) {
                                                isLoading = false
                                                dialog = BridgeDialog.PairingCode(
                                                    platform = platform,
                                                    code = code,
                                                    caption = stringProvider.getString(R.string.screen_bridge_settings_pairing_code_caption),
                                                )
                                                shouldStop = false
                                            } else {
                                                when {
                                                    bodyLc.contains("successfully logged in") ||
                                                        bodyLc.contains("login successful") ||
                                                        bodyLc.contains("connected to whatsapp") ||
                                                        bodyLc.contains("sync complete") ||
                                                        bodyLc.startsWith("logged in as") -> {
                                                        isLoading = false
                                                        snackbarMessage = stringProvider.getString(R.string.screen_bridge_settings_snackbar_connected)
                                                        extractIdentifier(body, platform)?.let { id ->
                                                            bridgeIdentifiers = bridgeIdentifiers.toMutableMap().apply { put(platform, id) }
                                                        }
                                                        shouldStop = true
                                                    }
                                                    bodyLc.contains("invalid phone") ||
                                                        bodyLc.contains("not a valid phone number") ||
                                                        bodyLc.contains("phone number is not registered") -> {
                                                        showError(stringProvider.getString(R.string.screen_bridge_settings_error_invalid_phone))
                                                        shouldStop = true
                                                    }
                                                    bodyLc.contains("login timed out") ||
                                                        bodyLc.contains("pairing code expired") ||
                                                        bodyLc.contains("login cancelled") -> {
                                                        showError(stringProvider.getString(R.string.screen_bridge_settings_error_code_expired))
                                                        shouldStop = true
                                                    }
                                                    else -> { shouldStop = false }
                                                }
                                            }
                                        } else {
                                            when {
                                                bodyLc.contains("successfully logged in") ||
                                                    bodyLc.contains("login successful") ||
                                                    bodyLc.contains("connected to whatsapp") ||
                                                    bodyLc.contains("sync complete") ||
                                                    bodyLc.startsWith("logged in as") -> {
                                                    isLoading = false
                                                    snackbarMessage = stringProvider.getString(R.string.screen_bridge_settings_snackbar_connected)
                                                    extractIdentifier(body, platform)?.let { id ->
                                                        bridgeIdentifiers = bridgeIdentifiers.toMutableMap().apply { put(platform, id) }
                                                    }
                                                    shouldStop = true
                                                }
                                                bodyLc.contains("invalid phone") ||
                                                    bodyLc.contains("not a valid phone number") ||
                                                    bodyLc.contains("phone number is not registered") -> {
                                                    showError(stringProvider.getString(R.string.screen_bridge_settings_error_invalid_phone))
                                                    shouldStop = true
                                                }
                                                bodyLc.contains("login timed out") ||
                                                    bodyLc.contains("pairing code expired") ||
                                                    bodyLc.contains("login cancelled") -> {
                                                    showError(stringProvider.getString(R.string.screen_bridge_settings_error_code_expired))
                                                    shouldStop = true
                                                }
                                                else -> { shouldStop = false }
                                            }
                                        }
                                    } else {
                                        shouldStop = false
                                    }
                                }
                                is BotEvent.Image -> { shouldStop = false }
                            }
                            shouldStop
                        }.collect { }
                        true
                    }
                    if (completed != true) {
                        showError(stringProvider.getString(R.string.screen_bridge_settings_error_request_timed_out))
                    }
                } catch (t: Throwable) {
                    if (t is kotlinx.coroutines.CancellationException) throw t
                    Timber.w(t, "BridgeSettings: observation failed for $platform")
                    showError(stringProvider.getString(R.string.screen_bridge_settings_error_unknown, t.localizedMessage ?: stringProvider.getString(R.string.screen_bridge_settings_error_unknown)))
                }
            }
        }

        fun handleEvent(event: BridgeSettingsEvents) {
            when (event) {
                is BridgeSettingsEvents.ConnectBridge -> {
                    activeJob?.cancel()
                    isLoading = true
                    snackbarMessage = null
                    dialog = null

                    activeJob = coroutineScope.launch {
                        try {
                            val botUserId = UserId(
                                when (event.platform) {
                                    "whatsapp" -> BuildConfig.WHATSAPP_BRIDGE_BOT
                                    else -> return@launch
                                }
                            )
                            val roomId = withTimeoutOrNull(30_000L) {
                                interactor.openBotRoom(botUserId).getOrThrow()
                            } ?: throw IllegalStateException(stringProvider.getString(R.string.screen_bridge_settings_error_server_connect))

                            activeRoomId = roomId
                            activeBotId = botUserId

                            // Check if already logged in — if so, auto-logout to allow reconnection
                            interactor.sendCommand(roomId, "list-logins").getOrThrow()
                            var loginId: String? = null
                            withTimeoutOrNull(10_000L) {
                                interactor.observeBotEvents(
                                    roomId = roomId,
                                    botUserId = botUserId,
                                    skipExisting = true,
                                ) { evt ->
                                    if (evt is BotEvent.Text) {
                                        val bodyLc = evt.body.lowercase()
                                        if (bodyLc.contains("not logged in") || bodyLc.contains("you're not logged in")) {
                                            return@observeBotEvents true
                                        }
                                        val idMatch = Regex("""\*\s+`([^`]+)`""").find(evt.body)
                                        if (idMatch != null) {
                                            loginId = idMatch.groupValues[1].trim()
                                            return@observeBotEvents true
                                        }
                                    }
                                    false
                                }.collect { }
                            }
                            if (loginId != null) {
                                interactor.sendCommand(roomId, "logout $loginId").getOrThrow()
                                withTimeoutOrNull(10_000L) {
                                    interactor.observeBotEvents(
                                        roomId = roomId,
                                        botUserId = botUserId,
                                        skipExisting = true,
                                    ) { evt ->
                                        if (evt is BotEvent.Text) {
                                            val bodyLc = evt.body.lowercase()
                                            bodyLc.contains("logged out") || bodyLc.contains("success")
                                        } else false
                                    }.collect { }
                                }
                            }

                            when (event.platform) {
                                "whatsapp" -> {
                                    isLoading = false
                                    dialog = BridgeDialog.PhoneInput(
                                        platform = "whatsapp",
                                        prompt = stringProvider.getString(R.string.screen_bridge_settings_phone_dialog_prompt),
                                        inputLabel = stringProvider.getString(R.string.screen_bridge_settings_phone_dialog_label),
                                        placeholder = stringProvider.getString(R.string.screen_bridge_settings_phone_dialog_placeholder),
                                    )
                                }
                            }
                        } catch (t: Throwable) {
                            if (t is kotlinx.coroutines.CancellationException) throw t
                            Timber.w(t, "BridgeSettings: connect failed for ${event.platform}")
                            showError(stringProvider.getString(R.string.screen_bridge_settings_error_unknown, t.localizedMessage ?: stringProvider.getString(R.string.screen_bridge_settings_error_unknown)))
                        }
                    }
                }

                is BridgeSettingsEvents.SubmitPhone -> {
                    val roomId = activeRoomId ?: return
                    val botUserId = activeBotId ?: return
                    activeJob?.cancel()
                    isLoading = true
                    dialog = null

                    activeJob = coroutineScope.launch {
                        try {
                            val normalized = normalizePhone(event.phone)
                            if (normalized.isBlank() || !normalized.startsWith("+")) {
                                showError(stringProvider.getString(R.string.screen_bridge_settings_error_phone_required))
                                return@launch
                            }
                            lastPhoneNumber = normalized
                            val command = "login phone $normalized"
                            interactor.sendCommand(roomId, command).getOrThrow()
                            startObservingBotEvents(roomId, botUserId, "whatsapp")
                        } catch (t: Throwable) {
                            if (t is kotlinx.coroutines.CancellationException) throw t
                            Timber.w(t, "BridgeSettings: phone submit failed")
                            showError(stringProvider.getString(R.string.screen_bridge_settings_error_unknown, t.localizedMessage ?: stringProvider.getString(R.string.screen_bridge_settings_error_unknown)))
                        }
                    }
                }

                is BridgeSettingsEvents.RequestNewCode -> {
                    val roomId = activeRoomId ?: return
                    val botUserId = activeBotId ?: return
                    val phone = lastPhoneNumber
                    if (phone.isNullOrBlank()) {
                        showError(stringProvider.getString(R.string.screen_bridge_settings_error_phone_info_missing))
                        return
                    }
                    activeJob?.cancel()
                    isLoading = true
                    dialog = null

                    activeJob = coroutineScope.launch {
                        try {
                            val command = "login phone $phone"
                            interactor.sendCommand(roomId, command).getOrThrow()
                            startObservingBotEvents(roomId, botUserId, "whatsapp")
                        } catch (t: Throwable) {
                            if (t is kotlinx.coroutines.CancellationException) throw t
                            Timber.w(t, "BridgeSettings: request new code failed")
                            showError(stringProvider.getString(R.string.screen_bridge_settings_error_unknown, t.localizedMessage ?: stringProvider.getString(R.string.screen_bridge_settings_error_unknown)))
                        }
                    }
                }

                is BridgeSettingsEvents.DisconnectBridge -> {
                    activeJob?.cancel()
                    isLoading = true
                    dialog = null
                    snackbarMessage = null

                    activeJob = coroutineScope.launch {
                        try {
                            val botUserId = UserId(
                                when (event.platform) {
                                    "whatsapp" -> BuildConfig.WHATSAPP_BRIDGE_BOT
                                    else -> return@launch
                                }
                            )
                            val roomId = withTimeoutOrNull(30_000L) {
                                interactor.openBotRoom(botUserId).getOrThrow()
                            } ?: throw IllegalStateException(stringProvider.getString(R.string.screen_bridge_settings_error_room_not_found))

                            // bridgev2 logout requires a login ID: first list-logins, then logout <id>
                            var reallyLoggedOut = false
                            var loginId: String? = null

                            // 1. List logins to get the login ID
                            interactor.sendCommand(roomId, "list-logins").getOrThrow()
                            withTimeoutOrNull(10_000L) {
                                interactor.observeBotEvents(
                                    roomId = roomId,
                                    botUserId = botUserId,
                                    skipExisting = true,
                                ) { evt ->
                                    if (evt is BotEvent.Text) {
                                        val body = evt.body
                                        val bodyLc = body.lowercase()
                                        if (bodyLc.contains("not logged in") || bodyLc.contains("you're not logged in")) {
                                            reallyLoggedOut = true
                                            return@observeBotEvents true
                                        }
                                        // Parse login ID from backticks: * `9055...` (name) - `STATE`
                                        val idMatch = Regex("""\*\s+`([^`]+)`""").find(body)
                                        if (idMatch != null) {
                                            loginId = idMatch.groupValues[1].trim()
                                            return@observeBotEvents true
                                        }
                                    }
                                    false
                                }.collect { }
                            }

                            // 2. If we got a login ID, send logout <id>
                            if (!reallyLoggedOut && !loginId.isNullOrBlank()) {
                                interactor.sendCommand(roomId, "logout $loginId").getOrThrow()
                                withTimeoutOrNull(10_000L) {
                                    interactor.observeBotEvents(
                                        roomId = roomId,
                                        botUserId = botUserId,
                                        skipExisting = true,
                                    ) { evt ->
                                        if (evt is BotEvent.Text) {
                                            val bodyLc = evt.body.lowercase()
                                            if (bodyLc.contains("logged out") ||
                                                bodyLc.contains("not logged in")
                                            ) {
                                                reallyLoggedOut = true
                                                return@observeBotEvents true
                                            }
                                        }
                                        false
                                    }.collect { }
                                }
                            }

                            // Always clear local state so UI updates immediately
                            forcedStatuses = forcedStatuses.toMutableMap().apply { put(event.platform, false) }
                            bridgeIdentifiers = bridgeIdentifiers.toMutableMap().apply { remove(event.platform) }
                            snackbarMessage = if (reallyLoggedOut) {
                                stringProvider.getString(R.string.screen_bridge_settings_snackbar_disconnected)
                            } else {
                                stringProvider.getString(R.string.screen_bridge_settings_snackbar_disconnect_uncertain)
                            }
                            isLoading = false
                        } catch (t: Throwable) {
                            if (t is kotlinx.coroutines.CancellationException) throw t
                            Timber.w(t, "BridgeSettings: disconnect failed for ${event.platform}")
                            showError(stringProvider.getString(R.string.screen_bridge_settings_error_unknown, t.localizedMessage ?: stringProvider.getString(R.string.screen_bridge_settings_error_unknown)))
                        }
                    }
                }

                is BridgeSettingsEvents.DismissSnackbar -> {
                    snackbarMessage = null
                }

                is BridgeSettingsEvents.DismissDialog -> {
                    dialog = null
                    activeJob?.cancel()
                    isLoading = false
                }
            }
        }

        return BridgeSettingsState(
            bridges = bridges,
            isLoading = isLoading,
            snackbarMessage = snackbarMessage,
            dialog = dialog,
            eventSink = ::handleEvent,
        )
    }

    private fun normalizePhone(input: String): String {
        val trimmed = input.trim()
        val digitsOnly = trimmed.filter { it.isDigit() }
        return when {
            digitsOnly.isEmpty() -> ""
            trimmed.startsWith("+") -> "+$digitsOnly"
            digitsOnly.startsWith("00") -> "+${digitsOnly.removePrefix("00")}"
            else -> "+$digitsOnly"
        }
    }

    private fun extractPairingCode(body: String): String? {
        val trimmed = body.trim()
        if (trimmed.length <= 16) {
            val cleaned = trimmed.filter { it.isLetterOrDigit() }
            if (looksLikePairingCode(cleaned)) return formatPairingCode(cleaned)
        }
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

    private fun looksLikePairingCode(s: String): Boolean {
        if (s.length !in 6..8) return false
        val upper = s.uppercase()
        val hasDigit = upper.any { it.isDigit() }
        val allAlnum = upper.all { it.isDigit() || it.isUpperCase() }
        return hasDigit && allAlnum
    }

    private fun formatPairingCode(s: String): String {
        val mid = s.length / 2
        return s.substring(0, mid).uppercase() + "-" + s.substring(mid).uppercase()
    }

    private fun extractIdentifier(body: String, platform: String): String? {
        return when (platform) {
            "whatsapp" -> {
                // Match patterns like "logged in as +905551112233" or "connected as +90 555 111 22 33"
                val regex = Regex("""(?:logged in|connected)(?:\s+as)?\s*([+\d\s()-]{10,20})""", RegexOption.IGNORE_CASE)
                regex.find(body)?.groupValues?.get(1)?.trim()?.replace(Regex("""[\s()-]"""), "")
            }

            else -> null
        }
    }
}
