package io.prism.android.features.ftue.impl.wizard

import io.prism.android.libraries.architecture.AsyncAction

data class MoneroWalletState(
    val address: String = "",
    val createAction: AsyncAction<Unit> = AsyncAction.Uninitialized,
    val eventSink: (MoneroWalletEvents) -> Unit,
)

sealed interface MoneroWalletEvents {
    data object CreateWallet : MoneroWalletEvents
    data object Skip : MoneroWalletEvents
    data object Back : MoneroWalletEvents
    data object Continue : MoneroWalletEvents
}
