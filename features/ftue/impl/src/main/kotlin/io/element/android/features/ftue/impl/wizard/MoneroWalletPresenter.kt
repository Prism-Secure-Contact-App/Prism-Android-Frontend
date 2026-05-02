package io.prism.android.features.ftue.impl.wizard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import io.prism.android.libraries.architecture.AsyncAction
import io.prism.android.libraries.architecture.Presenter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

class MoneroWalletPresenter(
    private val onWalletCreated: () -> Unit,
    private val onSkip: () -> Unit,
    private val onBack: () -> Unit,
) : Presenter<MoneroWalletState> {

    @Composable
    override fun present(): MoneroWalletState {
        val coroutineScope = rememberCoroutineScope()
        val address = remember { mutableStateOf("") }
        val mnemonic = remember { mutableStateOf("") }
        val viewKey = remember { mutableStateOf("") }
        val spendKey = remember { mutableStateOf("") }
        val createAction = remember { mutableStateOf<AsyncAction<Unit>>(AsyncAction.Uninitialized) }

        fun handleEvent(event: MoneroWalletEvents) {
            when (event) {
                MoneroWalletEvents.CreateWallet -> {
                    coroutineScope.launch {
                        createAction.value = AsyncAction.Loading
                        delay(2000) // Simulate wallet generation
                        address.value = "48v8k...fake_address..." + UUID.randomUUID().toString().take(8)
                        mnemonic.value = "apple banana cherry dog elephant fox grape hotel ice joke kite lemon"
                        viewKey.value = UUID.randomUUID().toString().replace("-", "")
                        spendKey.value = UUID.randomUUID().toString().replace("-", "")
                        createAction.value = AsyncAction.Success(Unit)
                    }
                }
                MoneroWalletEvents.Skip -> onSkip()
                MoneroWalletEvents.Back -> onBack()
                MoneroWalletEvents.Continue -> onWalletCreated()
            }
        }

        return MoneroWalletState(
            address = address.value,
            mnemonic = mnemonic.value,
            viewKey = viewKey.value,
            spendKey = spendKey.value,
            createAction = createAction.value,
            eventSink = ::handleEvent
        )
    }
}
