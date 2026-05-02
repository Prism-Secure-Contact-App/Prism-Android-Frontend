/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.call.ui

import app.cash.molecule.RecompositionMode
import app.cash.molecule.moleculeFlow
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import im.vector.app.features.analytics.plan.MobileScreen
import io.prism.android.features.call.api.CallType
import io.prism.android.features.call.impl.ui.CallScreenEvents
import io.prism.android.features.call.impl.ui.CallScreenNavigator
import io.prism.android.features.call.impl.ui.CallScreenPresenter
import io.prism.android.features.call.impl.utils.WidgetMessageSerializer
import io.prism.android.features.call.utils.FakeActiveCallManager
import io.prism.android.features.call.utils.FakeCallWidgetProvider
import io.prism.android.features.call.utils.FakeWidgetMessageInterceptor
import io.prism.android.libraries.androidutils.json.DefaultJsonProvider
import io.prism.android.libraries.architecture.AsyncData
import io.prism.android.libraries.core.coroutine.CoroutineDispatchers
import io.prism.android.libraries.matrix.api.sync.SyncState
import io.prism.android.libraries.matrix.test.A_ROOM_ID
import io.prism.android.libraries.matrix.test.A_SESSION_ID
import io.prism.android.libraries.matrix.test.FakePRISMClient
import io.prism.android.libraries.matrix.test.FakePRISMClientProvider
import io.prism.android.libraries.matrix.test.sync.FakeSyncService
import io.prism.android.libraries.matrix.test.widget.FakePRISMWidgetDriver
import io.prism.android.libraries.network.useragent.UserAgentProvider
import io.prism.android.services.analytics.api.ScreenTracker
import io.prism.android.services.analytics.test.FakeScreenTracker
import io.prism.android.services.appnavstate.test.FakeAppForegroundStateService
import io.prism.android.services.toolbox.api.systemclock.SystemClock
import io.prism.android.tests.testutils.WarmUpRule
import io.prism.android.tests.testutils.lambda.lambdaRecorder
import io.prism.android.tests.testutils.lambda.value
import io.prism.android.tests.testutils.testCoroutineDispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
class CallScreenPresenterTest {
    @get:Rule
    val warmUpRule = WarmUpRule()

    @Test
    fun `present - with CallType ExternalUrl just loads the URL and sets the call as active`() = runTest {
        val analyticsLambda = lambdaRecorder<MobileScreen.ScreenName, Unit> {}
        val joinedCallLambda = lambdaRecorder<CallType, Unit> {}
        val presenter = createCallScreenPresenter(
            callType = CallType.ExternalUrl("https://call.prism.io"),
            screenTracker = FakeScreenTracker(analyticsLambda),
            activeCallManager = FakeActiveCallManager(joinedCallResult = joinedCallLambda),
        )
        moleculeFlow(RecompositionMode.Immediate) {
            presenter.present()
        }.test {
            // Wait until the URL is loaded
            advanceTimeBy(1.seconds)
            skipItems(2)
            val initialState = awaitItem()
            assertThat(initialState.urlState).isEqualTo(AsyncData.Success("https://call.prism.io"))
            assertThat(initialState.webViewError).isNull()
            assertThat(initialState.isInWidgetMode).isFalse()
            assertThat(initialState.isCallActive).isFalse()
            analyticsLambda.assertions().isNeverCalled()
            joinedCallLambda.assertions().isCalledOnce()
        }
    }

    @Test
    fun `present - with CallType RoomCall sets call as active, loads URL and runs WidgetDriver`() = runTest {
        val widgetDriver = FakePRISMWidgetDriver()
        val widgetProvider = FakeCallWidgetProvider(widgetDriver)
        val analyticsLambda = lambdaRecorder<MobileScreen.ScreenName, Unit> {}
        val joinedCallLambda = lambdaRecorder<CallType, Unit> {}
        val presenter = createCallScreenPresenter(
            callType = CallType.RoomCall(A_SESSION_ID, A_ROOM_ID, false),
            widgetDriver = widgetDriver,
            widgetProvider = widgetProvider,
            screenTracker = FakeScreenTracker(analyticsLambda),
            activeCallManager = FakeActiveCallManager(joinedCallResult = joinedCallLambda),
        )
        moleculeFlow(RecompositionMode.Immediate) {
            presenter.present()
        }.test {
            // Wait until the URL is loaded
            advanceTimeBy(1.seconds)
            skipItems(1)

            joinedCallLambda.assertions().isCalledOnce()
            val initialState = awaitItem()
            assertThat(initialState.urlState).isInstanceOf(AsyncData.Loading::class.java)
            assertThat(initialState.isCallActive).isFalse()
            assertThat(initialState.isInWidgetMode).isTrue()
            assertThat(widgetProvider.getWidgetCalled).isTrue()
            assertThat(widgetDriver.runCalledCount).isEqualTo(1)
            analyticsLambda.assertions().isCalledOnce().with(value(MobileScreen.ScreenName.RoomCall))

            // Wait until the WidgetDriver is loaded
            skipItems(1)

            assertThat(awaitItem().urlState).isInstanceOf(AsyncData.Success::class.java)
        }
    }

    @Test
    fun `present - set message interceptor, send and receive messages`() = runTest {
        val widgetDriver = FakePRISMWidgetDriver()
        val presenter = createCallScreenPresenter(
            callType = CallType.RoomCall(A_SESSION_ID, A_ROOM_ID, false),
            widgetDriver = widgetDriver,
            screenTracker = FakeScreenTracker {},
        )
        val messageInterceptor = FakeWidgetMessageInterceptor()
        moleculeFlow(RecompositionMode.Immediate) {
            presenter.present()
        }.test {
            // Give it time to load the URL and WidgetDriver
            advanceTimeBy(1.seconds)

            val initialState = awaitItem()
            initialState.eventSink(CallScreenEvents.SetupMessageChannels(messageInterceptor))

            // And incoming message from the Widget Driver is passed to the WebView
            widgetDriver.givenIncomingMessage("A message")
            assertThat(messageInterceptor.sentMessages).containsExactly("A message")

            // And incoming message from the WebView is passed to the Widget Driver
            messageInterceptor.givenInterceptedMessage("A reply")
            assertThat(widgetDriver.sentMessages).containsExactly("A reply")

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `present - hang up event closes the screen and stops the widget driver`() = runTest(UnconfinedTestDispatcher()) {
        val navigator = FakeCallScreenNavigator()
        val widgetDriver = FakePRISMWidgetDriver()
        val presenter = createCallScreenPresenter(
            callType = CallType.RoomCall(A_SESSION_ID, A_ROOM_ID, false),
            widgetDriver = widgetDriver,
            navigator = navigator,
            dispatchers = testCoroutineDispatchers(useUnconfinedTestDispatcher = true),
            screenTracker = FakeScreenTracker {},
        )
        val messageInterceptor = FakeWidgetMessageInterceptor()
        moleculeFlow(RecompositionMode.Immediate) {
            presenter.present()
        }.test {
            val initialState = awaitItem()

            // Give it time to load the URL and WidgetDriver
            advanceTimeBy(1.seconds)

            initialState.eventSink(CallScreenEvents.SetupMessageChannels(messageInterceptor))

            initialState.eventSink(CallScreenEvents.Hangup)

            // Let background coroutines run and the widget drive be received
            runCurrent()

            assertThat(navigator.closeCalled).isTrue()
            assertThat(widgetDriver.closeCalledCount).isEqualTo(1)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `present - a received close message closes the screen and stops the widget driver`() = runTest(UnconfinedTestDispatcher()) {
        val navigator = FakeCallScreenNavigator()
        val widgetDriver = FakePRISMWidgetDriver()
        val presenter = createCallScreenPresenter(
            callType = CallType.RoomCall(A_SESSION_ID, A_ROOM_ID, false),
            widgetDriver = widgetDriver,
            navigator = navigator,
            dispatchers = testCoroutineDispatchers(useUnconfinedTestDispatcher = true),
            screenTracker = FakeScreenTracker {},
        )
        val messageInterceptor = FakeWidgetMessageInterceptor()
        moleculeFlow(RecompositionMode.Immediate) {
            presenter.present()
        }.test {
            val initialState = awaitItem()

            // Give it time to load the URL and WidgetDriver
            advanceTimeBy(1.seconds)

            initialState.eventSink(CallScreenEvents.SetupMessageChannels(messageInterceptor))

            messageInterceptor.givenInterceptedMessage("""{"action":"io.prism.close","api":"fromWidget","widgetId":"1","requestId":"1"}""")

            // Let background coroutines run
            advanceTimeBy(1.seconds)
            runCurrent()

            assertThat(navigator.closeCalled).isTrue()
            assertThat(widgetDriver.closeCalledCount).isEqualTo(1)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `present - a received 'content loaded' action makes the call to be active`() = runTest {
        val navigator = FakeCallScreenNavigator()
        val widgetDriver = FakePRISMWidgetDriver()
        val presenter = createCallScreenPresenter(
            callType = CallType.RoomCall(A_SESSION_ID, A_ROOM_ID, false),
            widgetDriver = widgetDriver,
            navigator = navigator,
            dispatchers = testCoroutineDispatchers(useUnconfinedTestDispatcher = true),
            screenTracker = FakeScreenTracker {},
        )
        val messageInterceptor = FakeWidgetMessageInterceptor()
        moleculeFlow(RecompositionMode.Immediate) {
            presenter.present()
        }.test {
            // Give it time to load the URL and WidgetDriver
            advanceTimeBy(1.seconds)
            skipItems(2)
            val initialState = awaitItem()
            assertThat(initialState.isCallActive).isFalse()
            initialState.eventSink(CallScreenEvents.SetupMessageChannels(messageInterceptor))
            messageInterceptor.givenInterceptedMessage(
                """
                    {
                        "action":"content_loaded",
                        "api":"fromWidget",
                        "widgetId":"1",
                        "requestId":"1"
                    }
                """.trimIndent()
            )
            skipItems(2)
            val finalState = awaitItem()
            assertThat(finalState.isCallActive).isTrue()
        }
    }

    @Test
    fun `present - if in room mode and no join action is received an error is displayed`() = runTest {
        val navigator = FakeCallScreenNavigator()
        val widgetDriver = FakePRISMWidgetDriver()
        val presenter = createCallScreenPresenter(
            callType = CallType.RoomCall(A_SESSION_ID, A_ROOM_ID, false),
            widgetDriver = widgetDriver,
            navigator = navigator,
            dispatchers = testCoroutineDispatchers(useUnconfinedTestDispatcher = true),
            screenTracker = FakeScreenTracker {},
        )
        val messageInterceptor = FakeWidgetMessageInterceptor()
        moleculeFlow(RecompositionMode.Immediate) {
            presenter.present()
        }.test {
            // Give it time to load the URL and WidgetDriver
            advanceTimeBy(1.seconds)
            skipItems(2)
            val initialState = awaitItem()
            assertThat(initialState.isCallActive).isFalse()
            initialState.eventSink(CallScreenEvents.SetupMessageChannels(messageInterceptor))
            skipItems(2)

            // Wait for the timeout to trigger
            advanceTimeBy(10.seconds)

            val finalState = awaitItem()
            assertThat(finalState.isCallActive).isFalse()
            // The error dialog that will force the user to leave the call is displayed
            assertThat(finalState.webViewError).isNotNull()
            assertThat(finalState.webViewError).isEmpty()
        }
    }

    @Test
    fun `present - automatically sets the isInCall state when starting the call and disposing the screen`() = runTest {
        val navigator = FakeCallScreenNavigator()
        val widgetDriver = FakePRISMWidgetDriver()
        val startSyncLambda = lambdaRecorder<Result<Unit>> { Result.success(Unit) }
        val syncService = FakeSyncService(SyncState.Idle).apply {
            this.startSyncLambda = startSyncLambda
        }
        val matrixClient = FakePRISMClient(syncService = syncService)
        val appForegroundStateService = FakeAppForegroundStateService()
        val presenter = createCallScreenPresenter(
            callType = CallType.RoomCall(A_SESSION_ID, A_ROOM_ID, false),
            widgetDriver = widgetDriver,
            navigator = navigator,
            dispatchers = testCoroutineDispatchers(useUnconfinedTestDispatcher = true),
            prismClientsProvider = FakePRISMClientProvider(getClient = { Result.success(matrixClient) }),
            screenTracker = FakeScreenTracker {},
            appForegroundStateService = appForegroundStateService,
        )
        val hasRun = Mutex(true)
        val job = launch {
            moleculeFlow(RecompositionMode.Immediate) {
                presenter.present()
            }.collect {
                hasRun.unlock()
            }
        }

        appForegroundStateService.isInCall.test {
            // The initial isInCall state will always be false
            assertThat(awaitItem()).isFalse()

            // Wait until the call starts
            hasRun.lock()

            // Then it'll be true once the call is active
            assertThat(awaitItem()).isTrue()

            // If we dispose the screen
            job.cancelAndJoin()

            // The isInCall state is now false
            assertThat(awaitItem()).isFalse()

            // And there are no more events
            ensureAllEventsConsumed()
        }
    }

    @Test
    fun `present - error from WebView are updating the state`() = runTest {
        val presenter = createCallScreenPresenter(
            callType = CallType.ExternalUrl("https://call.prism.io"),
            activeCallManager = FakeActiveCallManager(),
        )
        moleculeFlow(RecompositionMode.Immediate) {
            presenter.present()
        }.test {
            // Wait until the URL is loaded
            advanceTimeBy(1.seconds)
            skipItems(2)
            val initialState = awaitItem()
            initialState.eventSink(CallScreenEvents.OnWebViewError("A Webview error"))
            val finalState = awaitItem()
            assertThat(finalState.webViewError).isEqualTo("A Webview error")
        }
    }

    @Test
    fun `present - error from WebView are ignored if PRISM Call is loaded`() = runTest {
        val presenter = createCallScreenPresenter(
            callType = CallType.ExternalUrl("https://call.prism.io"),
            activeCallManager = FakeActiveCallManager(),
        )
        moleculeFlow(RecompositionMode.Immediate) {
            presenter.present()
        }.test {
            // Wait until the URL is loaded
            skipItems(1)
            val initialState = awaitItem()

            val messageInterceptor = FakeWidgetMessageInterceptor()
            initialState.eventSink(CallScreenEvents.SetupMessageChannels(messageInterceptor))
            // Emit a message
            messageInterceptor.givenInterceptedMessage("A message")
            // WebView emits an error, but it will be ignored
            initialState.eventSink(CallScreenEvents.OnWebViewError("A Webview error"))
            val finalState = awaitItem()
            assertThat(finalState.webViewError).isNull()

            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun TestScope.createCallScreenPresenter(
        callType: CallType,
        navigator: CallScreenNavigator = FakeCallScreenNavigator(),
        widgetDriver: FakePRISMWidgetDriver = FakePRISMWidgetDriver(),
        widgetProvider: FakeCallWidgetProvider = FakeCallWidgetProvider(widgetDriver),
        dispatchers: CoroutineDispatchers = testCoroutineDispatchers(),
        prismClientsProvider: FakePRISMClientProvider = FakePRISMClientProvider(),
        activeCallManager: FakeActiveCallManager = FakeActiveCallManager(),
        screenTracker: ScreenTracker = FakeScreenTracker(),
        appForegroundStateService: FakeAppForegroundStateService = FakeAppForegroundStateService(),
    ): CallScreenPresenter {
        val userAgentProvider = object : UserAgentProvider {
            override fun provide(): String {
                return "Test"
            }
        }
        val clock = SystemClock { 0 }
        return CallScreenPresenter(
            callType = callType,
            navigator = navigator,
            callWidgetProvider = widgetProvider,
            userAgentProvider = userAgentProvider,
            clock = clock,
            dispatchers = dispatchers,
            prismClientsProvider = prismClientsProvider,
            activeCallManager = activeCallManager,
            screenTracker = screenTracker,
            languageTagProvider = FakeLanguageTagProvider("en-US"),
            appForegroundStateService = appForegroundStateService,
            appCoroutineScope = backgroundScope,
            widgetMessageSerializer = WidgetMessageSerializer(DefaultJsonProvider()),
        )
    }
}
