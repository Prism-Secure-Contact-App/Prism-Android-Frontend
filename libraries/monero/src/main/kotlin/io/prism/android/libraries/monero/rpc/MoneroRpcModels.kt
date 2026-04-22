package io.prism.android.libraries.monero.rpc

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MoneroRpcRequest(
    @SerialName("jsonrpc") val jsonrpc: String = "2.0",
    @SerialName("id") val id: String = "0",
    @SerialName("method") val method: String,
    @SerialName("params") val params: MoneroRpcParams = MoneroRpcParams.None
)

@Serializable
sealed interface MoneroRpcParams {
    @Serializable
    data object None : MoneroRpcParams

    @Serializable
    data class TransferSplit(
        @SerialName("destinations") val destinations: List<MoneroDestination>,
        @SerialName("account_index") val accountIndex: Int = 0,
        @SerialName("priority") val priority: Int = 1, // 1 = unimportant, 2 = normal, 3 = elevated, 4 = priority
        @SerialName("mixin") val mixin: Int = 10,
        @SerialName("get_tx_keys") val getTxKeys: Boolean = true,
    ) : MoneroRpcParams
}

@Serializable
data class MoneroDestination(
    @SerialName("amount") val amountAtomic: Long,
    @SerialName("address") val address: String
)

@Serializable
data class MoneroRpcResponse<T>(
    @SerialName("jsonrpc") val jsonrpc: String,
    @SerialName("id") val id: String,
    @SerialName("result") val result: T? = null,
    @SerialName("error") val error: MoneroRpcError? = null
)

@Serializable
data class MoneroRpcError(
    @SerialName("code") val code: Int,
    @SerialName("message") val message: String
)

@Serializable
data class TransferSplitResult(
    @SerialName("tx_hash_list") val txHashList: List<String>,
    @SerialName("fee_list") val feeList: List<Long>,
    @SerialName("amount_list") val amountList: List<Long>,
    @SerialName("tx_key_list") val txKeyList: List<String>
)
