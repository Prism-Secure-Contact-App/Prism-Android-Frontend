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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import io.prism.android.libraries.architecture.AsyncAction
import io.prism.android.libraries.architecture.Presenter
import io.prism.android.libraries.matrix.api.PRISMClient
import io.prism.android.libraries.matrix.api.core.RoomId
import io.prism.android.libraries.matrix.api.core.UserId
import io.prism.android.libraries.matrix.api.media.MediaSource
import io.prism.android.libraries.ui.strings.CommonStrings
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import java.util.concurrent.atomic.AtomicReference
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
        val bridgeErrorMessage = stringResource(CommonStrings.error_ftue_bridge_unknown)
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
                        connectAction.value = AsyncAction.Failure(IllegalStateException(bridgeErrorMessage))
                        phase = UiPhase.Error(error)
                    },
                )
            }
        }

        fun handleWebViewError(message: String) {
            loginJob?.cancel()
            val error = IllegalStateException(message)
            connectAction.value = AsyncAction.Failure(error)
            phase = UiPhase.Error(message.asTextResource())
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
            pendingPrompt = pendingPrompt,
            eventSink = ::handleEvent,
        )
    }

    private suspend fun runConnection(
        interactor: BridgeBotInteractor,
        promptText: String?,
        onPhase: (UiPhase) -> Unit,
        onSuccess: () -> Unit,
        onFailure: (TextResource) -> Unit,
    ) {
        try {
            val botUserId = UserId(flow.botUserId)

            // 30 second timeout for the initial room lookup/creation. If this hangs,
            // the user stays on "Connecting" indefinitely.
            val roomId = withTimeoutOrNull(30_000L) {
                interactor.openBotRoom(botUserId).getOrThrow()
            } ?: run {
                onFailure(TextResource.Res(CommonStrings.error_ftue_bridge_no_connection))
                return
            }

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

            // 120-second total window. We show a live countdown progress bar so the user
            // knows the app is still working rather than silently freezing.
            val totalMillis = 120_000L
            var finalOutcome: Outcome = Outcome.Pending
            val currentPhaseRef = AtomicReference<UiPhase>(UiPhase.Connecting)
            val wrappedOnPhase: (UiPhase) -> Unit = { newPhase ->
                currentPhaseRef.set(newPhase)
                onPhase(newPhase)
            }

            coroutineScope {
                val observeJob = launch {
                    interactor.observeBotEvents(
                        roomId = roomId,
                        botUserId = botUserId,
                        stopWhen = { event ->
                            val decision = flow.classify(event)
                            when (decision) {
                                is FlowDecision.ShowQr -> {
                                    wrappedOnPhase(UiPhase.AwaitingScan(decision.source, decision.caption))
                                    false
                                }
                                is FlowDecision.AskForInput -> {
                                    wrappedOnPhase(UiPhase.AwaitingInput(decision.prompt, decision.inputLabel, decision.inputPlaceholder, decision.initialValue, decision.keyboardType))
                                    false
                                }
                                is FlowDecision.ShowPairingCode -> {
                                    wrappedOnPhase(UiPhase.AwaitingPairingCode(decision.code, decision.caption))
                                    false
                                }
                                is FlowDecision.OpenWebView -> {
                                    wrappedOnPhase(UiPhase.AwaitingWebView(decision.url, decision.userAgent, decision.cookieDomain, decision.cookieNames, decision.successUrlPattern, decision.caption))
                                    false
                                }
                                is FlowDecision.Progress -> {
                                    wrappedOnPhase(UiPhase.Working(decision.message))
                                    false
                                }
                                FlowDecision.Success -> { finalOutcome = Outcome.Success; true }
                                is FlowDecision.Failure -> { finalOutcome = Outcome.Failure(decision.message); true }
                                FlowDecision.Ignore -> false
                            }
                        },
                    ).collect { /* terminal in stopWhen */ }
                }

                val timerJob = launch {
                    val startTime = System.currentTimeMillis()
                    while (isActive) {
                        delay(1_000L)
                        val elapsed = System.currentTimeMillis() - startTime
                        if (elapsed >= totalMillis) {
                            observeJob.cancel()
                            finalOutcome = Outcome.Failure(TextResource.Res(CommonStrings.error_ftue_bridge_timeout))
                            break
                        }
                        val remainingSec = ((totalMillis - elapsed) / 1_000).toInt()
                        val progress = elapsed.toFloat() / totalMillis
                        val phase = currentPhaseRef.get()
                        when (phase) {
                            is UiPhase.Connecting, is UiPhase.Working -> {
                                wrappedOnPhase(UiPhase.Working(TextResource.Res(CommonStrings.screen_ftue_bridge_connecting_with_time, listOf(remainingSec)), progress))
                            }
                            is UiPhase.AwaitingPairingCode -> {
                                wrappedOnPhase(phase.copy(remainingSeconds = remainingSec))
                            }
                            else -> {}
                        }
                    }
                }

                observeJob.join()
                timerJob.cancel()
            }

            val terminalOutcome = finalOutcome
            when (terminalOutcome) {
                Outcome.Success -> {
                    try {
                        flow.onSetupComplete(matrixClient, roomId)
                    } catch (t: Throwable) {
                        Timber.w(t, "BridgePresenter: onSetupComplete failed for ${flow.displayName}")
                    }
                    onSuccess()
                }
                is Outcome.Failure -> onFailure(terminalOutcome.reason)
                Outcome.Pending -> {
                    // Cooperative timeout fell through without a terminal classification;
                    // treat as failure rather than leaving the user staring at a blank screen.
                    onFailure(TextResource.Res(CommonStrings.error_ftue_bridge_no_response))
                }
            }
        } catch (t: Throwable) {
            // CancellationException is rethrown automatically because we re-throw it from
            // the catch site below; everything else is reported to the UI layer.
            if (t is kotlinx.coroutines.CancellationException) throw t
            Timber.w(t, "BridgePresenter: connection failed for ${flow.displayName}")
            onFailure(t.message?.asTextResource() ?: TextResource.Res(CommonStrings.error_ftue_bridge_unknown))
        }
    }

    private sealed interface Outcome {
        data object Pending : Outcome
        data object Success : Outcome
        data class Failure(val reason: TextResource) : Outcome
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
    /** If non-null and [initialCommand] is empty, the wizard asks the user for input
     *  before sending anything to the bot. Used by WhatsApp pairing-code flow. */
    val initialPrompt: FlowDecision.AskForInput? get() = null

    /** Build the follow-up command when the user submits a [BridgeEvents.SubmitPrompt]. */
    fun followUpCommand(input: String): String

    /** Decide what the UI should do for an incoming bot event. */
    fun classify(event: BotEvent): FlowDecision

    /**
     * Called when the bridge setup completes successfully so the flow can create
     * a dedicated space and move the bot room into it.
     */
    suspend fun onSetupComplete(matrixClient: PRISMClient, botRoomId: RoomId) {}
}

internal sealed interface FlowDecision {
    data class ShowQr(val source: MediaSource, val caption: TextResource) : FlowDecision
    data class AskForInput(
        val prompt: TextResource,
        val inputLabel: TextResource = TextResource.Res(CommonStrings.screen_ftue_bridge_response_label),
        val inputPlaceholder: TextResource = TextResource.Plain(""),
        val initialValue: String = "",
        val keyboardType: KeyboardType = KeyboardType.Text,
    ) : FlowDecision
    data class ShowPairingCode(val code: String, val caption: TextResource) : FlowDecision

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
        val caption: TextResource,
    ) : FlowDecision

    data class Progress(val message: TextResource) : FlowDecision
    data object Success : FlowDecision
    data class Failure(val message: TextResource) : FlowDecision
    /** Ignore this bot reply (e.g. echoed prompt, command-not-found chatter). */
    data object Ignore : FlowDecision
}

/* ----------------------------- UI MODEL --------------------------------- */

internal sealed interface UiPhase {
    data object Idle : UiPhase
    data object Connecting : UiPhase
    data class Working(val message: TextResource, val progress: Float = -1f) : UiPhase
    data class AwaitingScan(val qrSource: MediaSource, val caption: TextResource) : UiPhase
    data class AwaitingInput(
        val prompt: TextResource,
        val inputLabel: TextResource = TextResource.Res(CommonStrings.screen_ftue_bridge_response_label),
        val inputPlaceholder: TextResource = TextResource.Plain(""),
        val initialValue: String = "",
        val keyboardType: KeyboardType = KeyboardType.Text,
    ) : UiPhase
    data class AwaitingPairingCode(val code: String, val caption: TextResource, val remainingSeconds: Int = -1) : UiPhase
    data class AwaitingWebView(
        val url: String,
        val userAgent: String,
        val cookieDomain: String,
        val cookieNames: List<String>,
        val successUrlPattern: String,
        val caption: TextResource,
    ) : UiPhase
    data class Error(val message: TextResource) : UiPhase
}

internal data class BridgeState(
    val bridgeName: String,
    val connectAction: AsyncAction<Unit>,
    val phase: UiPhase,
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
