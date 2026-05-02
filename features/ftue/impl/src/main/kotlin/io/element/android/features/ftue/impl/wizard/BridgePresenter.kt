package io.prism.android.features.ftue.impl.wizard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import io.prism.android.libraries.architecture.AsyncAction
import io.prism.android.libraries.architecture.Presenter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class BridgePresenter(
    private val bridgeName: String,
    private val onConnected: () -> Unit,
    private val onSkip: () -> Unit,
    private val onBack: () -> Unit,
) : Presenter<BridgeState> {

    @Composable
    override fun present(): BridgeState {
        val coroutineScope = rememberCoroutineScope()
        val connectAction = remember { mutableStateOf<AsyncAction<Unit>>(AsyncAction.Uninitialized) }

        fun handleEvent(event: BridgeEvents) {
            when (event) {
                BridgeEvents.Connect -> {
                    coroutineScope.launch {
                        connectAction.value = AsyncAction.Loading
                        delay(2000) // Simulate connection
                        connectAction.value = AsyncAction.Success(Unit)
                        delay(500)
                        onConnected()
                    }
                }
                BridgeEvents.Skip -> onSkip()
                BridgeEvents.Back -> onBack()
            }
        }

        return BridgeState(
            bridgeName = bridgeName,
            connectAction = connectAction.value,
            eventSink = ::handleEvent
        )
    }
}

data class BridgeState(
    val bridgeName: String,
    val connectAction: AsyncAction<Unit>,
    val eventSink: (BridgeEvents) -> Unit,
)

sealed interface BridgeEvents {
    data object Connect : BridgeEvents
    data object Skip : BridgeEvents
    data object Back : BridgeEvents
}
