/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 */

package io.prism.android.features.home.impl.prismai.data

import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.prism.android.features.home.impl.prismai.api.PrismAIApiKeyResponse
import io.prism.android.features.home.impl.prismai.api.PrismAIApiService
import io.prism.android.features.home.impl.prismai.api.PrismAIChatHistoryResponse
import io.prism.android.features.home.impl.prismai.api.PrismAIChatRequest
import io.prism.android.features.home.impl.prismai.api.PrismAIChatResponse
import io.prism.android.features.home.impl.prismai.api.PrismAILiveMessagesResponse
import io.prism.android.features.home.impl.prismai.api.PrismAIRoomsResponse
import io.prism.android.libraries.di.SessionScope
import io.prism.android.libraries.matrix.api.PRISMClient
import io.prism.android.libraries.network.RetrofitFactory
import io.prism.android.libraries.sessionstorage.api.SessionStore
import kotlinx.coroutines.flow.first
import timber.log.Timber
import java.net.URL

@Inject
@SingleIn(SessionScope::class)
class PrismAIRepository(
    private val sessionStore: SessionStore,
    private val matrixClient: PRISMClient,
    private val retrofitFactory: RetrofitFactory,
) {

    private var cachedApiService: PrismAIApiService? = null

    private suspend fun getAccessToken(): String? {
        return try {
            val sessions = sessionStore.sessionsFlow().first()
            val sessionId = matrixClient.sessionId.value
            sessions.firstOrNull { it.userId == sessionId }?.accessToken
        } catch (t: Throwable) {
            Timber.e(t, "PrismAIRepository: failed to get access token")
            null
        }
    }

    private suspend fun getBaseUrl(): String {
        val sessions = sessionStore.sessionsFlow().first()
        val sessionId = matrixClient.sessionId.value
        val homeserverUrl = sessions.firstOrNull { it.userId == sessionId }?.homeserverUrl
            ?: "https://matrix.fathertkt.uk"
        return try {
            val url = URL(homeserverUrl)
            // Use the same host but port 8080 for the LLM API service
            "${url.protocol}://${url.host}:8080/"
        } catch (t: Throwable) {
            Timber.w(t, "PrismAIRepository: failed to parse homeserver URL, using fallback")
            "https://matrix.fathertkt.uk:8080/"
        }
    }

    private suspend fun apiService(): PrismAIApiService {
        cachedApiService?.let { return it }
        val baseUrl = getBaseUrl()
        val service = retrofitFactory.create(baseUrl).create(PrismAIApiService::class.java)
        cachedApiService = service
        return service
    }

    private suspend fun bearerToken(): String {
        val token = getAccessToken()
        requireNotNull(token) { "No Matrix access token available" }
        return "Bearer $token"
    }

    suspend fun sendChatMessage(message: String, roomId: String? = null): Result<PrismAIChatResponse> {
        return try {
            val response = apiService().sendChatMessage(bearerToken(), PrismAIChatRequest(message, roomId))
            Result.success(response)
        } catch (t: Throwable) {
            Timber.e(t, "PrismAIRepository: chat message failed")
            Result.failure(t)
        }
    }

    suspend fun getPrismaiRooms(): Result<PrismAIRoomsResponse> {
        return try {
            val response = apiService().getPrismaiRooms(bearerToken())
            Result.success(response)
        } catch (t: Throwable) {
            Timber.e(t, "PrismAIRepository: get rooms failed")
            Result.failure(t)
        }
    }

    suspend fun getChatHistory(roomId: String, limit: Int = 50): Result<PrismAIChatHistoryResponse> {
        return try {
            val response = apiService().getChatHistory(bearerToken(), roomId, limit)
            Result.success(response)
        } catch (t: Throwable) {
            Timber.e(t, "PrismAIRepository: get chat history failed")
            Result.failure(t)
        }
    }

    suspend fun getLiveChatHistory(roomId: String, limit: Int = 50): Result<PrismAILiveMessagesResponse> {
        return try {
            val response = apiService().getLiveChatHistory(bearerToken(), roomId, limit)
            Result.success(response)
        } catch (t: Throwable) {
            Timber.e(t, "PrismAIRepository: get live chat history failed")
            Result.failure(t)
        }
    }

    suspend fun createApiKey(): Result<PrismAIApiKeyResponse> {
        return try {
            val response = apiService().createApiKey(bearerToken())
            Result.success(response)
        } catch (t: Throwable) {
            Timber.e(t, "PrismAIRepository: create API key failed")
            Result.failure(t)
        }
    }

    suspend fun getApiKey(): Result<PrismAIApiKeyResponse> {
        return try {
            val response = apiService().getApiKey(bearerToken())
            Result.success(response)
        } catch (t: Throwable) {
            Timber.e(t, "PrismAIRepository: get API key failed")
            Result.failure(t)
        }
    }

    suspend fun deleteApiKey(): Result<PrismAIApiKeyResponse> {
        return try {
            val response = apiService().deleteApiKey(bearerToken())
            Result.success(response)
        } catch (t: Throwable) {
            Timber.e(t, "PrismAIRepository: delete API key failed")
            Result.failure(t)
        }
    }
}
