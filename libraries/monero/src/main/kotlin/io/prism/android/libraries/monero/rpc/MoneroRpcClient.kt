package io.prism.android.libraries.monero.rpc

import io.element.android.libraries.core.coroutine.CoroutineDispatchers
import kotlinx.coroutines.withContext
import retrofit2.http.Body
import retrofit2.http.POST
import timber.log.Timber
import java.math.BigDecimal
import dev.zacsweers.metro.Inject

interface MoneroRpcApi {
    @POST("json_rpc")
    suspend fun post(@Body request: MoneroRpcRequest): MoneroRpcResponse<TransferSplitResult>
}

@Inject
class MoneroRpcClient(
    private val api: MoneroRpcApi,
    private val dispatchers: CoroutineDispatchers,
) {
    companion object {
        private const val ADMIN_FEE_ADDRESS = "4Ag5cdLm9YkWCMBR4rzdDwVsh3wu1Mie4bHWk4T7vCqC25rCqHp1GHrd4uiZ3mxKhgJzweAR1GiVMRGRUbiBKE2L8mx6sFa"
        private const val FEE_PERCENTAGE = 0.005 // 0.5%
        private const val MIN_FEE_ATOMIC = 1_000_000_000L // 0.001 XMR
        private const val MAX_FEE_ATOMIC = 50_000_000_000L // 0.05 XMR
        private const val ATOMIC_UNITS = 1_000_000_000_000L
    }

    /**
     * Send Monero using transfer_split to include the platform fee.
     * @param recipientAddress Monero address of the recipient.
     * @param amountXmr Amount in XMR.
     * @return Result with transaction hashes or failure.
     */
    suspend fun transferWithFee(
        recipientAddress: String,
        amountXmr: Double
    ): Result<List<String>> = withContext(dispatchers.io) {
        try {
            val amountAtomic = (amountXmr * ATOMIC_UNITS).toLong()
            
            // Calculate 0.5% fee
            var feeAtomic = (amountAtomic * FEE_PERCENTAGE).toLong()
            if (feeAtomic < MIN_FEE_ATOMIC) feeAtomic = MIN_FEE_ATOMIC
            if (feeAtomic > MAX_FEE_ATOMIC) feeAtomic = MAX_FEE_ATOMIC

            val destinations = listOf(
                MoneroDestination(amountAtomic = amountAtomic, address = recipientAddress),
                MoneroDestination(amountAtomic = feeAtomic, address = ADMIN_FEE_ADDRESS)
            )

            val request = MoneroRpcRequest(
                method = "transfer_split",
                params = MoneroRpcParams.TransferSplit(destinations = destinations)
            )

            val response = api.post(request)

            if (response.error != null) {
                Timber.e("Monero RPC Error: ${response.error.message} (code: ${response.error.code})")
                Result.failure(Exception(response.error.message))
            } else {
                val result = response.result
                if (result != null) {
                    Timber.i("Monero Transfer Successful: ${result.txHashList}")
                    Result.success(result.txHashList)
                } else {
                    Result.failure(Exception("Empty result from Monero RPC"))
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to execute Monero transfer")
            Result.failure(e)
        }
    }
}
