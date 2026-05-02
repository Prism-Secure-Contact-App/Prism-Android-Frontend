/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 */

package io.prism.android.features.login.impl.screens.createaccount

import io.prism.android.libraries.matrix.api.auth.external.ExternalSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

/**
 * Direct, app-side Synapse account registration.
 *
 * v1.0.0 design choice: PRISM creates accounts directly against the Synapse REST API
 * using the password flow with a single `m.login.dummy` auth stage. No e-mail / SMS /
 * captcha verification, no WebView, no OIDC. The 2FA flow is deferred to v1.0.1.
 *
 * Endpoint contract (Matrix Spec v1.11):
 *   POST {homeserver}/_matrix/client/v3/register
 *
 * On the *first* call Synapse can return HTTP 401 with a JSON body listing the
 * required `flows`. We retry once with the `auth: { type: "m.login.dummy", session }`
 * stage to satisfy the simplest enabled flow.
 *
 * Failure modes surfaced to the UI:
 * - `M_USER_IN_USE`              -> "Bu kullanıcı adı zaten alınmış"
 * - `M_INVALID_USERNAME`          -> "Geçersiz kullanıcı adı"
 * - `M_WEAK_PASSWORD`             -> "Parola çok zayıf"
 * - `M_FORBIDDEN`                 -> "Bu sunucuda kayıt kapalı"
 * - any other HTTP / I/O failure  -> raw error message
 */
internal class SynapseRegisterClient(
    private val homeserverUrl: String,
) {
    private val httpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val json: Json = Json { ignoreUnknownKeys = true }

    /**
     * Register a brand new account and return an [ExternalSession] that can be imported into the
     * PRISM Rust SDK via `PRISMAuthenticationService.importCreatedSession`.
     */
    suspend fun register(username: String, password: String): Result<ExternalSession> = withContext(Dispatchers.IO) {
        runCatching {
            val firstAttempt = postRegister(buildBody(username, password, session = null))
            val finalResponse = if (firstAttempt.code == 401) {
                // Synapse returned the required flows; pull session id and replay with the
                // `m.login.dummy` stage. This is the canonical "no captcha / no email" path.
                val session = parseSessionFromFlows(firstAttempt.body)
                postRegister(buildBody(username, password, session = session))
            } else {
                firstAttempt
            }

            if (!finalResponse.isSuccessful) {
                throw mapError(finalResponse)
            }

            val payload = json.parseToJsonElement(finalResponse.body) as JsonObject
            val userId = payload["user_id"]?.jsonString() ?: error("user_id missing in register response")
            val accessToken = payload["access_token"]?.jsonString() ?: error("access_token missing in register response")
            val deviceId = payload["device_id"]?.jsonString() ?: error("device_id missing in register response")
            ExternalSession(
                userId = userId,
                deviceId = deviceId,
                accessToken = accessToken,
                refreshToken = payload["refresh_token"]?.jsonString(),
                homeserverUrl = homeserverUrl,
            )
        }
    }

    private fun postRegister(body: String): HttpResult {
        val request = Request.Builder()
            .url("${homeserverUrl.trimEnd('/')}/_matrix/client/v3/register")
            .post(body.toRequestBody(JSON_MEDIA_TYPE))
            .build()
        httpClient.newCall(request).execute().use { resp ->
            return HttpResult(
                code = resp.code,
                isSuccessful = resp.isSuccessful,
                body = resp.body?.string().orEmpty(),
            )
        }
    }

    private fun buildBody(username: String, password: String, session: String?): String {
        val auth: JsonObject = buildJsonObject {
            put("type", "m.login.dummy")
            if (session != null) put("session", session)
        }
        val payload = buildJsonObject {
            put("username", username.trim())
            put("password", password)
            put("inhibit_login", false)
            put("auth", auth)
        }
        return payload.toString()
    }

    private fun parseSessionFromFlows(body: String): String {
        val obj = json.parseToJsonElement(body) as JsonObject
        return obj["session"]?.jsonString()
            ?: error("Synapse 401 response missing `session` field; cannot continue dummy flow")
    }

    private fun mapError(resp: HttpResult): Throwable {
        // Try to surface Synapse's standard error envelope: { "errcode": "...", "error": "..." }
        val parsed = runCatching { json.parseToJsonElement(resp.body) as? JsonObject }.getOrNull()
        val errcode = parsed?.get("errcode")?.jsonString()
        val error = parsed?.get("error")?.jsonString()
        val friendly = when (errcode) {
            "M_USER_IN_USE" -> "Bu kullanıcı adı zaten alınmış."
            "M_INVALID_USERNAME" -> "Geçersiz kullanıcı adı."
            "M_WEAK_PASSWORD" -> "Parola çok zayıf."
            "M_FORBIDDEN" -> "Bu sunucuda kayıt kapalı."
            "M_EXCLUSIVE" -> "Bu kullanıcı adı bir bridge tarafından rezerve edilmiş."
            else -> error ?: "Kayıt başarısız (HTTP ${resp.code})."
        }
        return RegisterException(friendly, errcode, resp.code)
    }

    private data class HttpResult(val code: Int, val isSuccessful: Boolean, val body: String)

    private fun kotlinx.serialization.json.JsonElement.jsonString(): String? {
        val prim = this as? JsonPrimitive ?: return null
        return if (prim.isString) prim.content else null
    }

    companion object {
        private val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()
    }
}

class RegisterException(
    message: String,
    val errcode: String?,
    val httpStatus: Int,
) : RuntimeException(message)
