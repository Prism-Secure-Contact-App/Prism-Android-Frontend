package io.prism.android.features.ftue.impl.wizard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import im.molly.monero.sdk.MoneroNetwork
import im.molly.monero.sdk.MoneroNodeClient
import im.molly.monero.sdk.RemoteNode
import im.molly.monero.sdk.service.InProcessWalletService
import im.molly.monero.sdk.singleNodeClient
import io.prism.android.libraries.architecture.AsyncAction
import io.prism.android.libraries.architecture.Presenter
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import java.io.File

class MoneroWalletPresenter(
    private val onWalletCreated: () -> Unit,
    private val onSkip: () -> Unit,
    private val onBack: () -> Unit,
) : Presenter<MoneroWalletState> {

    @Composable
    override fun present(): MoneroWalletState {
        val context = LocalContext.current
        val coroutineScope = rememberCoroutineScope()
        val address = remember { mutableStateOf("") }
        val createAction = remember { mutableStateOf<AsyncAction<Unit>>(AsyncAction.Uninitialized) }

        fun handleEvent(event: MoneroWalletEvents) {
            when (event) {
                MoneroWalletEvents.CreateWallet -> {
                    coroutineScope.launch {
                        createAction.value = AsyncAction.Loading
                        try {
                            val walletDir = File(context.filesDir, "prism_wallet")
                            walletDir.mkdirs()
                            val walletFile = File(walletDir, "monero_wallet.bin")
                            val dataStore = MoneroWalletDataStore(walletFile)

                            val walletProvider = InProcessWalletService.Companion.connect(context)

                            val remoteNode = RemoteNode(
                                "https://node.community.rino.io:18081",
                                MoneroNetwork.Mainnet
                            )
                            val nodeClient: MoneroNodeClient = remoteNode.singleNodeClient(OkHttpClient())

                            val wallet = walletProvider.createNewWallet(
                                MoneroNetwork.Mainnet,
                                dataStore,
                                nodeClient
                            )

                            address.value = wallet.publicAddress.address
                            wallet.save()
                            wallet.close()
                            walletProvider.disconnect()
                            createAction.value = AsyncAction.Success(Unit)
                        } catch (e: Exception) {
                            createAction.value = AsyncAction.Failure(e)
                        }
                    }
                }
                MoneroWalletEvents.Skip -> onSkip()
                MoneroWalletEvents.Back -> onBack()
                MoneroWalletEvents.Continue -> onWalletCreated()
            }
        }

        return MoneroWalletState(
            address = address.value,
            mnemonic = "",
            viewKey = "",
            spendKey = "",
            createAction = createAction.value,
            eventSink = ::handleEvent
        )
    }
}
