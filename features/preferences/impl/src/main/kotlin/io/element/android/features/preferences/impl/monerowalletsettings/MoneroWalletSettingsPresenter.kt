/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.preferences.impl.monerowalletsettings

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import dev.zacsweers.metro.Inject
import im.molly.monero.sdk.FeePriority
import im.molly.monero.sdk.MoneroAmount
import im.molly.monero.sdk.MoneroNetwork
import im.molly.monero.sdk.MoneroNodeClient
import im.molly.monero.sdk.MoneroWallet
import im.molly.monero.sdk.PaymentDetail
import im.molly.monero.sdk.PaymentRequest
import im.molly.monero.sdk.PublicAddress
import im.molly.monero.sdk.RemoteNode
import im.molly.monero.sdk.service.SandboxedWalletService
import im.molly.monero.sdk.singleNodeClient
import io.prism.android.libraries.architecture.AsyncAction
import io.prism.android.libraries.architecture.Presenter
import io.prism.android.libraries.di.annotations.ApplicationContext
import io.prism.android.libraries.matrix.api.PRISMClient
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import timber.log.Timber
import java.io.File
import java.math.BigDecimal

@Inject
class MoneroWalletSettingsPresenter(
    @ApplicationContext private val context: Context,
    private val matrixClient: PRISMClient,
) : Presenter<MoneroWalletSettingsState> {

    @Composable
    override fun present(): MoneroWalletSettingsState {
        val coroutineScope = rememberCoroutineScope()
        var isRevealed by remember { mutableStateOf(false) }
        var snackbarMessage by remember { mutableStateOf<String?>(null) }
        val address = remember { mutableStateOf<String?>(null) }
        val balance = remember { mutableStateOf("Loading...") }
        val feeRate = remember { mutableStateOf("Yükleniyor...") }
        var showWithdrawDialog by remember { mutableStateOf(false) }
        var withdrawAddress by remember { mutableStateOf("") }
        var withdrawAmount by remember { mutableStateOf("") }
        var withdrawAction by remember { mutableStateOf<AsyncAction<Unit>>(AsyncAction.Uninitialized) }

        var wallet by remember { mutableStateOf<MoneroWallet?>(null) }
        var walletProvider by remember { mutableStateOf<im.molly.monero.sdk.WalletProvider?>(null) }

        LaunchedEffect(Unit) {
            try {
                val walletFile = File(context.filesDir, "prism_wallet/monero_wallet.bin")
                if (!walletFile.exists()) {
                    balance.value = "Wallet not found"
                    feeRate.value = "—"
                    return@LaunchedEffect
                }
                val dataStore = MoneroWalletDataStore(walletFile)
                val provider = SandboxedWalletService.Companion.connect(context)
                walletProvider = provider
                val remoteNode = RemoteNode(
                    "https://node.community.rino.io:18081",
                    MoneroNetwork.Mainnet
                )
                val nodeClient: MoneroNodeClient = remoteNode.singleNodeClient(OkHttpClient())
                val w = provider.openWallet(
                    MoneroNetwork.Mainnet,
                    dataStore,
                    nodeClient
                )
                wallet = w
                address.value = w.publicAddress.address

                w.awaitRefresh()
                val ledger = w.ledger().first()
                val bal = ledger.getBalance()
                balance.value = BigDecimal.valueOf(bal.totalAmount.atomicUnits, MoneroAmount.ATOMIC_UNIT_SCALE).toPlainString() + " XMR"

                val fee = w.dynamicFeeRate().first()
                val mediumFeeAtomic = fee.feePerByte[FeePriority.Medium]?.atomicUnits ?: 0
                val mediumFee = BigDecimal.valueOf(mediumFeeAtomic, MoneroAmount.ATOMIC_UNIT_SCALE).toPlainString()
                feeRate.value = "$mediumFee XMR/byte"
            } catch (e: Exception) {
                Timber.e(e, "Monero wallet load failed")
                balance.value = "Error"
                feeRate.value = "Hata"
                snackbarMessage = e.message ?: "Unknown error"
            }
        }

        DisposableEffect(Unit) {
            onDispose {
                wallet?.close()
                walletProvider?.disconnect()
            }
        }

        fun handleEvent(event: MoneroWalletSettingsEvents) {
            when (event) {
                is MoneroWalletSettingsEvents.RevealSeedPhrase -> {
                    isRevealed = true
                }
                is MoneroWalletSettingsEvents.CopySeedPhrase -> {
                    snackbarMessage = "Recovery phrase is hidden by the SDK"
                }
                is MoneroWalletSettingsEvents.CopyAddress -> {
                    snackbarMessage = "Wallet address copied to clipboard"
                }
                is MoneroWalletSettingsEvents.CopyViewKey -> {
                    snackbarMessage = "View key is hidden by the SDK"
                }
                is MoneroWalletSettingsEvents.CopySpendKey -> {
                    snackbarMessage = "Spend key is hidden by the SDK"
                }
                is MoneroWalletSettingsEvents.DismissSnackbar -> {
                    snackbarMessage = null
                }
                is MoneroWalletSettingsEvents.ShowWithdrawDialog -> {
                    showWithdrawDialog = true
                }
                is MoneroWalletSettingsEvents.DismissWithdrawDialog -> {
                    showWithdrawDialog = false
                }
                is MoneroWalletSettingsEvents.SetWithdrawAddress -> {
                    withdrawAddress = event.address
                }
                is MoneroWalletSettingsEvents.SetWithdrawAmount -> {
                    withdrawAmount = event.amount
                }
                is MoneroWalletSettingsEvents.SubmitWithdraw -> {
                    coroutineScope.launch {
                        withdrawAction = AsyncAction.Loading
                        try {
                            val w = wallet ?: throw IllegalStateException("Wallet not loaded")
                            val recipient = PublicAddress.parse(withdrawAddress.trim())
                            val atomicUnits = BigDecimal(withdrawAmount.trim())
                                .times(BigDecimal.TEN.pow(MoneroAmount.ATOMIC_UNIT_SCALE))
                                .toLong()
                            val amount = MoneroAmount(atomicUnits)
                            val transfer = w.createTransfer(
                                PaymentRequest(
                                    paymentDetails = listOf(PaymentDetail(amount, recipient)),
                                    spendingAccountIndex = 0,
                                    feePriority = FeePriority.Medium,
                                )
                            )
                            transfer.commit()
                            transfer.close()
                            withdrawAction = AsyncAction.Success(Unit)
                            snackbarMessage = "Transfer sent"
                            showWithdrawDialog = false
                            withdrawAddress = ""
                            withdrawAmount = ""
                            // Refresh balance
                            w.awaitRefresh()
                            val ledger = w.ledger().first()
                            balance.value = BigDecimal.valueOf(ledger.getBalance().totalAmount.atomicUnits, MoneroAmount.ATOMIC_UNIT_SCALE).toPlainString() + " XMR"
                        } catch (e: Exception) {
                            Timber.e(e, "Withdraw failed")
                            withdrawAction = AsyncAction.Failure(e)
                            snackbarMessage = e.message ?: "Transfer failed"
                        }
                    }
                }
            }
        }

        return MoneroWalletSettingsState(
            address = address.value,
            mnemonic = "Hidden by the SDK",
            viewKey = "SDK tarafından gizli tutulmaktadır",
            spendKey = "SDK tarafından gizli tutulmaktadır",
            balance = balance.value,
            feeRate = feeRate.value,
            isRevealed = isRevealed,
            snackbarMessage = snackbarMessage,
            showWithdrawDialog = showWithdrawDialog,
            withdrawAddress = withdrawAddress,
            withdrawAmount = withdrawAmount,
            withdrawAction = withdrawAction,
            eventSink = ::handleEvent,
        )
    }
}
