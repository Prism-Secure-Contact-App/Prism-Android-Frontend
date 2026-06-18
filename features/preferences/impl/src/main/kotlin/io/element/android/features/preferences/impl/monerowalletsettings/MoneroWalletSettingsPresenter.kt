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
import androidx.compose.runtime.MutableState
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
import im.molly.monero.sdk.WalletProvider
import im.molly.monero.sdk.service.SandboxedWalletService
import im.molly.monero.sdk.singleNodeClient
import io.prism.android.features.preferences.impl.BuildConfig
import io.prism.android.features.preferences.impl.monerowalletsettings.MoneroWalletDataStore
import io.prism.android.libraries.architecture.AsyncAction
import io.prism.android.libraries.architecture.Presenter
import io.prism.android.libraries.di.annotations.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import timber.log.Timber
import java.io.File
import java.math.BigDecimal

private const val MONERO_ATOMIC_UNIT_SCALE = 12

@Inject
class MoneroWalletSettingsPresenter(
    @ApplicationContext private val context: Context,
) : Presenter<MoneroWalletSettingsState> {

    @Composable
    override fun present(): MoneroWalletSettingsState {
        val coroutineScope = rememberCoroutineScope()
        var isRevealed by remember { mutableStateOf(false) }
        var snackbarMessage by remember { mutableStateOf<String?>(null) }
        val address = remember { mutableStateOf<String?>(null) }
        val mnemonic = remember { mutableStateOf<String?>(null) }
        val viewKey = remember { mutableStateOf<String?>(null) }
        val spendKey = remember { mutableStateOf<String?>(null) }
        val balance = remember { mutableStateOf("Loading...") }
        var balanceAtomicUnits by remember { mutableStateOf(0L) }
        val feeRate = remember { mutableStateOf("Loading...") }
        var showWithdrawDialog by remember { mutableStateOf(false) }
        var withdrawAddress by remember { mutableStateOf("") }
        var withdrawAmount by remember { mutableStateOf("") }
        var withdrawAction by remember { mutableStateOf<AsyncAction<Unit>>(AsyncAction.Uninitialized) }

        var wallet by remember { mutableStateOf<MoneroWallet?>(null) }
        var walletProvider by remember { mutableStateOf<WalletProvider?>(null) }

        LaunchedEffect(Unit) {
            loadWallet(
                context = context,
                address = address,
                mnemonic = mnemonic,
                viewKey = viewKey,
                spendKey = spendKey,
                balance = balance,
                feeRate = feeRate,
                onBalanceAtomicUnits = { balanceAtomicUnits = it },
                onWallet = { wallet = it },
                onProvider = { walletProvider = it },
                onError = { snackbarMessage = it },
            )
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
                    snackbarMessage = "Recovery phrase copied to clipboard"
                }
                is MoneroWalletSettingsEvents.CopyAddress -> {
                    snackbarMessage = "Wallet address copied to clipboard"
                }
                is MoneroWalletSettingsEvents.CopyViewKey -> {
                    snackbarMessage = "View key copied to clipboard"
                }
                is MoneroWalletSettingsEvents.CopySpendKey -> {
                    snackbarMessage = "Spend key copied to clipboard"
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
                            val error = validateWithdraw(
                                address = withdrawAddress.trim(),
                                amount = withdrawAmount.trim(),
                                balanceAtomicUnits = balanceAtomicUnits,
                            )
                            if (error != null) {
                                withdrawAction = AsyncAction.Failure(IllegalArgumentException(error))
                                snackbarMessage = error
                                return@launch
                            }

                            withContext(Dispatchers.IO) {
                                val w = wallet ?: throw IllegalStateException("Wallet not loaded")
                                val recipient = PublicAddress.parse(withdrawAddress.trim())
                                val atomicUnits = BigDecimal(withdrawAmount.trim())
                                    .times(BigDecimal.TEN.pow(MONERO_ATOMIC_UNIT_SCALE))
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
                            }
                            withdrawAction = AsyncAction.Success(Unit)
                            snackbarMessage = "Transfer sent"
                            showWithdrawDialog = false
                            withdrawAddress = ""
                            withdrawAmount = ""
                            // Refresh balance
                            refreshBalance(wallet, balance) { balanceAtomicUnits = it }
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
            mnemonic = mnemonic.value,
            viewKey = viewKey.value,
            spendKey = spendKey.value,
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

    private suspend fun loadWallet(
        context: Context,
        address: MutableState<String?>,
        mnemonic: MutableState<String?>,
        viewKey: MutableState<String?>,
        spendKey: MutableState<String?>,
        balance: MutableState<String>,
        feeRate: MutableState<String>,
        onBalanceAtomicUnits: (Long) -> Unit,
        onWallet: (MoneroWallet) -> Unit,
        onProvider: (WalletProvider) -> Unit,
        onError: (String) -> Unit,
    ) = withContext(Dispatchers.IO) {
        try {
            val walletFile = File(context.filesDir, "prism_wallet/monero_wallet.bin")
            if (!walletFile.exists()) {
                balance.value = "Wallet not found"
                feeRate.value = "—"
                return@withContext
            }
            val dataStore = MoneroWalletDataStore(context, walletFile)
            val provider = SandboxedWalletService.connect(context)
            onProvider(provider)
            val remoteNode = RemoteNode(
                BuildConfig.MONERO_REMOTE_NODE,
                resolveNetwork(BuildConfig.MONERO_NETWORK),
            )
            val nodeClient: MoneroNodeClient = remoteNode.singleNodeClient(OkHttpClient())
            val w = provider.openWallet(
                resolveNetwork(BuildConfig.MONERO_NETWORK),
                dataStore,
                nodeClient,
            )
            onWallet(w)
            address.value = w.publicAddress.address
            mnemonic.value = runCatching { w.mnemonic }.getOrNull()
            viewKey.value = runCatching { w.viewKey }.getOrNull()
            spendKey.value = runCatching { w.spendKey }.getOrNull()

            w.awaitRefresh()
            val ledger = w.ledger().first()
            val bal = ledger.getBalance()
            onBalanceAtomicUnits(bal.totalAmount.atomicUnits)
            balance.value = formatXmr(bal.totalAmount.atomicUnits)

            val fee = w.dynamicFeeRate().first()
            val mediumFeeAtomic = fee.feePerByte[FeePriority.Medium]?.atomicUnits ?: 0
            feeRate.value = formatXmr(mediumFeeAtomic) + "/byte"
        } catch (e: Exception) {
            Timber.e(e, "Monero wallet load failed")
            balance.value = "Error"
            feeRate.value = "Error"
            onError(e.message ?: "Unknown error")
        }
    }

    private suspend fun refreshBalance(
        wallet: MoneroWallet?,
        balance: MutableState<String>,
        onBalanceAtomicUnits: (Long) -> Unit,
    ) = withContext(Dispatchers.IO) {
        try {
            val w = wallet ?: return@withContext
            w.awaitRefresh()
            val ledger = w.ledger().first()
            val bal = ledger.getBalance()
            onBalanceAtomicUnits(bal.totalAmount.atomicUnits)
            balance.value = formatXmr(bal.totalAmount.atomicUnits)
        } catch (e: Exception) {
            Timber.e(e, "Balance refresh failed")
        }
    }

    private fun validateWithdraw(
        address: String,
        amount: String,
        balanceAtomicUnits: Long,
    ): String? {
        if (address.isBlank()) return "Withdraw address is required"
        if (amount.isBlank()) return "Withdraw amount is required"

        val parsedAmount = try {
            BigDecimal(amount)
        } catch (e: NumberFormatException) {
            return "Invalid amount"
        }
        if (parsedAmount <= BigDecimal.ZERO) return "Amount must be positive"

        val atomicUnits = try {
            parsedAmount.times(BigDecimal.TEN.pow(MONERO_ATOMIC_UNIT_SCALE)).toLong()
        } catch (e: ArithmeticException) {
            return "Amount is too large"
        }
        if (atomicUnits > balanceAtomicUnits) return "Insufficient balance"

        return try {
            PublicAddress.parse(address)
            null
        } catch (e: Exception) {
            "Invalid Monero address"
        }
    }

    private fun formatXmr(atomicUnits: Long): String {
        return BigDecimal.valueOf(atomicUnits, MONERO_ATOMIC_UNIT_SCALE).toPlainString() + " XMR"
    }

    private fun resolveNetwork(value: String): MoneroNetwork = when (value.uppercase()) {
        "TESTNET" -> MoneroNetwork.Testnet
        "STAGENET" -> MoneroNetwork.Stagenet
        else -> MoneroNetwork.Mainnet
    }
}
