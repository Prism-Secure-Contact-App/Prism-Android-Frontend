/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 */

package io.prism.android.features.home.impl.prismai.api

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface PrismAIApiService {

    @POST("v1/prismai/chat")
    suspend fun sendChatMessage(
        @Header("Authorization") authorization: String,
        @Body request: PrismAIChatRequest,
    ): PrismAIChatResponse

    @GET("v1/prismai/rooms")
    suspend fun getPrismaiRooms(
        @Header("Authorization") authorization: String,
    ): PrismAIRoomsResponse

    @GET("v1/prismai/chat/history")
    suspend fun getChatHistory(
        @Header("Authorization") authorization: String,
        @Query("room_id") roomId: String,
        @Query("limit") limit: Int = 50,
    ): PrismAIChatHistoryResponse

    @GET("v1/prismai/chat/history/live")
    suspend fun getLiveChatHistory(
        @Header("Authorization") authorization: String,
        @Query("room_id") roomId: String,
        @Query("limit") limit: Int = 50,
    ): PrismAILiveMessagesResponse

    @POST("v1/llm/api-keys")
    suspend fun createApiKey(
        @Header("Authorization") authorization: String,
    ): PrismAIApiKeyResponse

    @GET("v1/llm/api-keys")
    suspend fun getApiKey(
        @Header("Authorization") authorization: String,
    ): PrismAIApiKeyResponse

    @DELETE("v1/llm/api-keys")
    suspend fun deleteApiKey(
        @Header("Authorization") authorization: String,
    ): PrismAIApiKeyResponse
}
