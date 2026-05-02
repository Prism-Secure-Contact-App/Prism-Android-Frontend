/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.pushproviders.unifiedpush

import android.content.Context
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import io.prism.android.libraries.di.annotations.ApplicationContext
import io.prism.android.libraries.matrix.api.PRISMClient
import io.prism.android.libraries.pushproviders.api.PusherSubscriber
import org.unifiedpush.android.connector.UnifiedPush
import timber.log.Timber

interface UnregisterUnifiedPushUseCase {
    /**
     * Unregister the app from the homeserver, then from UnifiedPush if [unregisterUnifiedPush] is true.
     */
    suspend fun unregister(
        matrixClient: PRISMClient,
        clientSecret: String,
        unregisterUnifiedPush: Boolean = true,
    ): Result<Unit>

    /**
     * Cleanup any remaining data for the given client secret and unregister the app from UnifiedPush.
     */
    fun cleanup(
        clientSecret: String,
        unregisterUnifiedPush: Boolean = true,
    )
}

@ContributesBinding(AppScope::class)
class DefaultUnregisterUnifiedPushUseCase(
    @ApplicationContext private val context: Context,
    private val unifiedPushStore: UnifiedPushStore,
    private val pusherSubscriber: PusherSubscriber,
) : UnregisterUnifiedPushUseCase {
    override suspend fun unregister(
        matrixClient: PRISMClient,
        clientSecret: String,
        unregisterUnifiedPush: Boolean,
    ): Result<Unit> {
        val endpoint = unifiedPushStore.getEndpoint(clientSecret)
        val gateway = unifiedPushStore.getPushGateway(clientSecret)
        if (endpoint == null || gateway == null) {
            Timber.w("No endpoint or gateway found for client secret")
            // Ensure we don't have any remaining data, but ignore this error
            cleanup(clientSecret)
            return Result.success(Unit)
        }
        return pusherSubscriber.unregisterPusher(matrixClient, endpoint, gateway)
            .onSuccess {
                cleanup(clientSecret, unregisterUnifiedPush)
            }
    }

    override fun cleanup(clientSecret: String, unregisterUnifiedPush: Boolean) {
        unifiedPushStore.storeUpEndpoint(clientSecret, null)
        unifiedPushStore.storePushGateway(clientSecret, null)
        if (unregisterUnifiedPush) {
            UnifiedPush.unregister(context, clientSecret)
        }
    }
}
