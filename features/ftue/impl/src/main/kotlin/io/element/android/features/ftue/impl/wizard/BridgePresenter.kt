/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 */

package io.prism.android.features.ftue.impl.wizard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardType
import io.prism.android.libraries.architecture.AsyncAction
import io.prism.android.libraries.architecture.Presenter
import io.prism.android.libraries.matrix.api.PRISMClient
import io.prism.android.libraries.matrix.api.core.UserId
import io.prism.android.libraries.matrix.api.media.MediaSource
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import timber.log.Timber

/**
 * Common presenter logic for both bridge wizard steps (WhatsApp + Meta).
 *
 * Each bridge has the exact same conversational shape with the bot:
 *   1. Open / create DM with the bot.
 *   2. Send a "login" command.
 *   3. The bot either replies with a QR-code image (WhatsApp) or with text
 *      asking for cookies / further input (Meta).
 *   4. We feed the bot's events through [BridgeFlow] which decides how the
 *      wizard's UI should react (`UiPhase`) and whether the step is done.
 *
 * The bridge-specific behaviour is encapsulated in [BridgeFlow] so this
 * presenter stays bridge-agnostic and avoids the v0.x mock that just
 * delayed 2 s and pretended to succeed.
 */
internal class BridgePresenter(
    private val matrixClient: PRISMClient,
    private val flow: BridgeFlow,
    private val onConnected: () -> Unit,
    private val onSkip: () -> Unit,
    private val onBack: () -> Unit,
) : Presenter<BridgeState> {

    @Composable
    override fun present(): BridgeState {
        val coroutineScope = rememberCoroutineScope()
        val connectAction = remember { mutableStateOf<AsyncAction<Unit>>(AsyncAction.Uninitialized) }
        var phase by remember { mutableStateOf<UiPhase>(UiPhase.Idle) }
        var pendingPrompt by remember { mutableStateOf<String?>(null) }
        var loginJob by remember { mutableStateOf<Job?>(null) }

        fun start(promptText: String? = null) {
            // Cancel any in-flight attempt so retries / re-entry are clean.
            loginJob?.cancel()
            connectAction.value = AsyncAction.Loading
            phase = UiPhase.Connecting
            loginJob = coroutineScope.launch {
                runConnection(
                    interactor = BridgeBotInteractor(matrixClient),
                    promptText = promptText,
                    onPhase = { phase = it },
                    onSuccess = {
                        connectAction.value = AsyncAction.Success(Unit)
                        onConnected()
                    },
                    onFailure = { error ->
                        connectAction.value = AsyncAction.Failure(error)
                        phase = UiPhase.Error(error.message ?: "Bilinmeyen hata")
                    },
                )
            }
        }

        fun handleWebViewError(message: String) {
            loginJob?.cancel()
            val error = IllegalStateException(message)
            connectAction.value = AsyncAction.Failure(error)
            phase = UiPhase.Error(message)
        }

        fun handleEvent(event: BridgeEvents) {
            when (event) {
                BridgeEvents.Connect -> start()
                BridgeEvents.Skip -> {
                    loginJob?.cancel()
                    onSkip()
                }
                BridgeEvents.Back -> {
                    loginJob?.cancel()
                    onBack()
                }
                is BridgeEvents.SubmitPrompt -> start(promptText = event.value)
                BridgeEvents.UpdatePrompt -> Unit
                is BridgeEvents.WebViewError -> handleWebViewError(event.message)
            }
        }

        return BridgeState(
            bridgeName = flow.displayName,
            connectAction = connectAction.value,
            phase = phase,
            promptDescription = flow.promptDescription,
            pendingPrompt = pendingPrompt,
            eventSink = ::handleEvent,
        )
    }

    private suspend fun runConnection(
        interactor: BridgeBotInteractor,
        promptText: String?,
        onPhase: (UiPhase) -> Unit,
        onSuccess: () -> Unit,
        onFailure: (Throwable) -> Unit,
    ) {
        try {
            val botUserId = UserId(flow.botUserId)
            
            // 30 second timeout for the initial room lookup/creation. If this hangs,
            // the user stays on "Connecting" indefinitely.
            val roomId = withTimeoutOrNull(30_000L) {
                interactor.openBotRoom(botUserId).getOrThrow()
            } ?: throw IllegalStateException("Sunucuya bağlanılamadı. Lütfen internetinizi kontrol edin.")

            // Some flows (e.g. WhatsApp pairing-code) need the user's input BEFORE
            // any bot interaction. We detect this by an empty initialCommand + a
            // declared initialPrompt, and surface the input UI without sending anything.
            if (promptText == null && flow.initialCommand.isEmpty()) {
                val seed = flow.initialPrompt
                if (seed != null) {
                    onPhase(UiPhase.AwaitingInput(seed.prompt, seed.inputLabel, seed.inputPlaceholder, seed.initialValue, seed.keyboardType))
                    return
                }
            }
            val command = if (promptText != null) flow.followUpCommand(promptText) else flow.initialCommand
            interactor.sendCommand(roomId, command).getOrThrow()

            // 90 seconds is generous enough for a human to scan a QR or paste cookies,
            // and short enough that a totally unresponsive bridge eventually surfaces an error.
            val outcome = withTimeoutOrNull(90_000L) {
                var finalOutcome: Outcome = Outcome.Pending
                interactor.observeBotEvents(
                    roomId = roomId,
                    botUserId = botUserId,
                    stopWhen = { event ->
                        val decision = flow.classify(event)
                        when (decision) {
                            is FlowDecision.ShowQr -> {
                                onPhase(UiPhase.AwaitingScan(decision.source, decision.caption))
                                false
                            }
                            is FlowDecision.AskForInput -> {
                                onPhase(UiPhase.AwaitingInput(decision.prompt, decision.inputLabel, decision.inputPlaceholder, decision.initialValue, decision.keyboardType))
                                false
                            }
                            is FlowDecision.ShowPairingCode -> {
                                onPhase(UiPhase.AwaitingPairingCode(decision.code, decision.caption))
                                false
                            }
                            is FlowDecision.OpenWebView -> {
                                onPhase(UiPhase.AwaitingWebView(decision.url, decision.userAgent, decision.cookieDomain, decision.cookieNames, decision.successUrlPattern, decision.caption))
                                false
                            }
                            is FlowDecision.Progress -> {
                                onPhase(UiPhase.Working(decision.message))
                                false
                            }
                            FlowDecision.Success -> { finalOutcome = Outcome.Success; true }
                            is FlowDecision.Failure -> { finalOutcome = Outcome.Failure(decision.message); true }
                            FlowDecision.Ignore -> false
                        }
                    },
                ).collect { /* terminal in stopWhen */ }
                finalOutcome
            } ?: Outcome.Failure("İstek zaman aşımına uğradı. Lütfen tekrar deneyin.")

            when (outcome) {
                Outcome.Success -> onSuccess()
                is Outcome.Failure -> onFailure(IllegalStateException(outcome.reason))
                Outcome.Pending -> {
                    // Cooperative timeout fell through without a terminal classification;
                    // treat as failure rather than leaving the user staring at a blank screen.
                    onFailure(IllegalStateException("Bridge yanıt vermedi. Tekrar deneyebilirsin."))
                }
            }
        } catch (t: Throwable) {
            // CancellationException is rethrown automatically because we re-throw it from
            // the catch site below; everything else is reported to the UI layer.
            if (t is kotlinx.coroutines.CancellationException) throw t
            Timber.w(t, "BridgePresenter: connection failed for ${flow.displayName}")
            onFailure(t)
        }
    }

    private sealed interface Outcome {
        data object Pending : Outcome
        data object Success : Outcome
        data class Failure(val reason: String) : Outcome
    }
}

/**
 * Bridge-specific glue: bot user id, command vocabulary, and a classifier
 * mapping bot replies to UI directives. Keeps `BridgePresenter` agnostic.
 */
internal interface BridgeFlow {
    val displayName: String
    val botUserId: String
    /** First command sent right after the DM is opened. Empty = collect user input first via [initialPrompt]. */
    val initialCommand: String
    /** Optional human-readable hint shown when the wizard asks for cookie/text input. */
    val promptDescription: String?
    /** If non-null and [initialCommand] is empty, the wizard asks the user for input
     *  before sending anything to the bot. Used by WhatsApp pairing-code flow. */
    val initialPrompt: FlowDecision.AskForInput? get() = null

    /** Build the follow-up command when the user submits a [BridgeEvents.SubmitPrompt]. */
    fun followUpCommand(input: String): String

    /** Decide what the UI should do for an incoming bot event. */
    fun classify(event: BotEvent): FlowDecision
}

internal sealed interface FlowDecision {
    data class ShowQr(val source: MediaSource, val caption: String) : FlowDecision
    data class AskForInput(
        val prompt: String,
        val inputLabel: String = "Yanıt",
        val inputPlaceholder: String = "",
        val initialValue: String = "",
        val keyboardType: KeyboardType = KeyboardType.Text,
    ) : FlowDecision
    data class ShowPairingCode(val code: String, val caption: String) : FlowDecision

    /**
     * Open an embedded WebView so the user can sign in to a remote service in-app, after which
     * the listed cookies are scraped, packed into a JSON object, and submitted to the bridge bot
     * as the next command. Used by Instagram (mautrix-meta cookie login) so users don't have to
     * copy-paste cookies from desktop devtools.
     */
    data class OpenWebView(
        val url: String,
        val userAgent: String,
        val cookieDomain: String,
        val cookieNames: List<String>,
        val successUrlPattern: String,
        val caption: String,
    ) : FlowDecision

    data class Progress(val message: String) : FlowDecision
    data object Success : FlowDecision
    data class Failure(val message: String) : FlowDecision
    /** Ignore this bot reply (e.g. echoed prompt, command-not-found chatter). */
    data object Ignore : FlowDecision
}

/* ----------------------------- UI MODEL --------------------------------- */

internal sealed interface UiPhase {
    data object Idle : UiPhase
    data object Connecting : UiPhase
    data class Working(val message: String) : UiPhase
    data class AwaitingScan(val qrSource: MediaSource, val caption: String) : UiPhase
    data class AwaitingInput(
        val prompt: String,
        val inputLabel: String = "Yanıt",
        val inputPlaceholder: String = "",
        val initialValue: String = "",
        val keyboardType: KeyboardType = KeyboardType.Text,
    ) : UiPhase
    data class AwaitingPairingCode(val code: String, val caption: String) : UiPhase
    data class AwaitingWebView(
        val url: String,
        val userAgent: String,
        val cookieDomain: String,
        val cookieNames: List<String>,
        val successUrlPattern: String,
        val caption: String,
    ) : UiPhase
    data class Error(val message: String) : UiPhase
}

internal data class BridgeState(
    val bridgeName: String,
    val connectAction: AsyncAction<Unit>,
    val phase: UiPhase,
    val promptDescription: String?,
    val pendingPrompt: String?,
    val eventSink: (BridgeEvents) -> Unit,
)

internal sealed interface BridgeEvents {
    data object Connect : BridgeEvents
    data object Skip : BridgeEvents
    data object Back : BridgeEvents
    data class SubmitPrompt(val value: String) : BridgeEvents
    data object UpdatePrompt : BridgeEvents
    data class WebViewError(val message: String) : BridgeEvents
}
