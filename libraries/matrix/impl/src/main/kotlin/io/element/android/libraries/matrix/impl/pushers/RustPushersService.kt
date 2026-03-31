/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.prism.impl.pushers

import io.prism.android.libraries.core.coroutine.CoroutineDispatchers
import io.prism.android.libraries.core.extensions.mapFailure
import io.prism.android.libraries.core.extensions.runCatchingExceptions
import io.prism.android.libraries.prism.api.pusher.PushersService
import io.prism.android.libraries.prism.api.pusher.SetHttpPusherData
import io.prism.android.libraries.prism.api.pusher.UnsetHttpPusherData
import io.prism.android.libraries.prism.impl.exception.mapClientException
import kotlinx.coroutines.withContext
import org.prism.rustcomponents.sdk.Client
import org.prism.rustcomponents.sdk.HttpPusherData
import org.prism.rustcomponents.sdk.PushFormat
import org.prism.rustcomponents.sdk.PusherIdentifiers
import org.prism.rustcomponents.sdk.PusherKind

class RustPushersService(
    private val client: Client,
    private val dispatchers: CoroutineDispatchers
) : PushersService {
    override suspend fun setHttpPusher(setHttpPusherData: SetHttpPusherData): Result<Unit> {
        return withContext(dispatchers.io) {
            runCatchingExceptions {
                client.setPusher(
                    identifiers = PusherIdentifiers(
                        pushkey = setHttpPusherData.pushKey,
                        appId = setHttpPusherData.appId
                    ),
                    kind = PusherKind.Http(
                        data = HttpPusherData(
                            url = setHttpPusherData.url,
                            format = PushFormat.EVENT_ID_ONLY,
                            defaultPayload = setHttpPusherData.defaultPayload
                        )
                    ),
                    appDisplayName = setHttpPusherData.appDisplayName,
                    deviceDisplayName = setHttpPusherData.deviceDisplayName,
                    profileTag = setHttpPusherData.profileTag,
                    lang = setHttpPusherData.lang
                )
            }
                .mapFailure { it.mapClientException() }
        }
    }

    override suspend fun unsetHttpPusher(unsetHttpPusherData: UnsetHttpPusherData): Result<Unit> {
        return withContext(dispatchers.io) {
            runCatchingExceptions {
                client.deletePusher(
                    identifiers = PusherIdentifiers(
                        pushkey = unsetHttpPusherData.pushKey,
                        appId = unsetHttpPusherData.appId
                    ),
                )
            }
        }
    }
}
