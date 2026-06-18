package io.prism.android.features.ftue.impl.wizard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import im.molly.monero.sdk.MoneroNetwork
import im.molly.monero.sdk.MoneroNodeClient
import im.molly.monero.sdk.MoneroWallet
import im.molly.monero.sdk.RemoteNode
import im.molly.monero.sdk.WalletProvider
import im.molly.monero.sdk.service.SandboxedWalletService
import im.molly.monero.sdk.singleNodeClient
import io.prism.android.features.ftue.impl.BuildConfig
import io.prism.android.libraries.architecture.AsyncAction
import io.prism.android.libraries.architecture.Presenter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
        var address by remember { mutableStateOf("") }
        var mnemonic by remember { mutableStateOf("") }
        var viewKey by remember { mutableStateOf("") }
        var spendKey by remember { mutableStateOf("") }
        var createAction by remember { mutableStateOf<AsyncAction<Unit>>(AsyncAction.Uninitialized) }
        var walletProvider by remember { mutableStateOf<WalletProvider?>(null) }
        var wallet by remember { mutableStateOf<MoneroWallet?>(null) }

        DisposableEffect(Unit) {
            onDispose {
                wallet?.close()
                walletProvider?.disconnect()
            }
        }

        fun handleEvent(event: MoneroWalletEvents) {
            when (event) {
                MoneroWalletEvents.CreateWallet -> {
                    coroutineScope.launch {
                        createAction = AsyncAction.Loading
                        try {
                            val result = withContext(Dispatchers.IO) {
                                val walletDir = File(context.filesDir, "prism_wallet")
                                walletDir.mkdirs()
                                val walletFile = File(walletDir, "monero_wallet.bin")
                                val dataStore = MoneroWalletDataStore(context, walletFile)

                                val provider = SandboxedWalletService.connect(context)
                                val remoteNode = RemoteNode(
                                    BuildConfig.MONERO_REMOTE_NODE,
                                    resolveNetwork(BuildConfig.MONERO_NETWORK),
                                )
                                val nodeClient: MoneroNodeClient = remoteNode.singleNodeClient(OkHttpClient())

                                val createdWallet = provider.createNewWallet(
                                    resolveNetwork(BuildConfig.MONERO_NETWORK),
                                    dataStore,
                                    nodeClient,
                                )
                                createdWallet.save()
                                CreatedWalletResult(
                                    wallet = createdWallet,
                                    provider = provider,
                                    address = createdWallet.publicAddress.address,
                                    mnemonic = createdWallet.mnemonic,
                                    viewKey = createdWallet.viewKey,
                                    spendKey = createdWallet.spendKey,
                                )
                            }
                            wallet = result.wallet
                            walletProvider = result.provider
                            address = result.address
                            mnemonic = result.mnemonic
                            viewKey = result.viewKey
                            spendKey = result.spendKey
                            createAction = AsyncAction.Success(Unit)
                        } catch (e: Exception) {
                            createAction = AsyncAction.Failure(e)
                        }
                    }
                }
                MoneroWalletEvents.Skip -> onSkip()
                MoneroWalletEvents.Back -> onBack()
                MoneroWalletEvents.Continue -> onWalletCreated()
            }
        }

        return MoneroWalletState(
            address = address,
            mnemonic = mnemonic,
            viewKey = viewKey,
            spendKey = spendKey,
            createAction = createAction,
            eventSink = ::handleEvent,
        )
    }

    private fun resolveNetwork(value: String): MoneroNetwork = when (value.uppercase()) {
        "TESTNET" -> MoneroNetwork.Testnet
        "STAGENET" -> MoneroNetwork.Stagenet
        else -> MoneroNetwork.Mainnet
    }

    private data class CreatedWalletResult(
        val wallet: MoneroWallet,
        val provider: WalletProvider,
        val address: String,
        val mnemonic: String,
        val viewKey: String,
        val spendKey: String,
    )
}
