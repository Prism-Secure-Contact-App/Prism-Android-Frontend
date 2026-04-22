package io.prism.android.libraries.monero

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import io.element.android.libraries.androidutils.json.JsonProvider
import io.prism.android.libraries.monero.rpc.MoneroRpcApi
import io.prism.android.libraries.monero.rpc.MoneroRpcClient
import okhttp3.Credentials
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

@BindingContainer
@ContributesTo(AppScope::class)
object MoneroModule {
    private const val MONERO_RPC_URL = "http://100.125.63.77:18083/"
    private const val RPC_USER = "fathertkt"
    private const val RPC_PASSWORD = "1234"

    @Provides
    @SingleIn(AppScope::class)
    fun providesMoneroRpcApi(
        baseOkHttpClient: OkHttpClient,
        jsonProvider: JsonProvider,
    ): MoneroRpcApi {
        val moneroClient = baseOkHttpClient.newBuilder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", Credentials.basic(RPC_USER, RPC_PASSWORD))
                    .build()
                chain.proceed(request)
            }
            .build()

        val contentType = "application/json".toMediaType()
        val retrofit = Retrofit.Builder()
            .baseUrl(MONERO_RPC_URL)
            .client(moneroClient)
            .addConverterFactory(jsonProvider.json.asConverterFactory(contentType))
            .build()

        return retrofit.create(MoneroRpcApi::class.java)
    }
}
