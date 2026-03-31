/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.prism.impl.media

import io.prism.android.libraries.core.extensions.runCatchingExceptions
import io.prism.android.libraries.prism.api.media.MediaPreviewConfig
import io.prism.android.libraries.prism.api.media.MediaPreviewService
import io.prism.android.libraries.prism.api.media.MediaPreviewValue
import io.prism.android.libraries.prism.impl.util.mxCallbackFlow
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import org.prism.rustcomponents.sdk.Client
import org.prism.rustcomponents.sdk.InviteAvatars
import org.prism.rustcomponents.sdk.MediaPreviewConfigListener
import org.prism.rustcomponents.sdk.MediaPreviews
import org.prism.rustcomponents.sdk.MediaPreviewConfig as RustMediaPreviewConfig

class RustMediaPreviewService(
    sessionCoroutineScope: CoroutineScope,
    private val sessionDispatcher: CoroutineDispatcher,
    private val innerClient: Client,
) : MediaPreviewService {
    override val mediaPreviewConfigFlow: StateFlow<MediaPreviewConfig> =
        innerClient
            .getMediaPreviewConfigFlow()
            .stateIn(sessionCoroutineScope, started = SharingStarted.Lazily, initialValue = MediaPreviewConfig.DEFAULT)

    override suspend fun fetchMediaPreviewConfig(): Result<MediaPreviewConfig?> = withContext(sessionDispatcher) {
        runCatchingExceptions {
            innerClient.fetchMediaPreviewConfig()?.into()
        }
    }

    override suspend fun setMediaPreviewValue(mediaPreviewValue: MediaPreviewValue): Result<Unit> = withContext(sessionDispatcher) {
        runCatchingExceptions {
            innerClient.setMediaPreviewDisplayPolicy(mediaPreviewValue.into())
        }
    }

    override suspend fun setHideInviteAvatars(hide: Boolean): Result<Unit> = withContext(sessionDispatcher) {
        runCatchingExceptions {
            val inviteAvatars = if (hide) InviteAvatars.OFF else InviteAvatars.ON
            innerClient.setInviteAvatarsDisplayPolicy(inviteAvatars)
        }
    }
}

private fun RustMediaPreviewConfig.into(): MediaPreviewConfig {
    return MediaPreviewConfig(
        mediaPreviewValue = mediaPreviews.into(),
        hideInviteAvatar = inviteAvatars == InviteAvatars.OFF
    )
}

private fun Client.getMediaPreviewConfigFlow() = mxCallbackFlow {
    subscribeToMediaPreviewConfig(object : MediaPreviewConfigListener {
        override fun onChange(mediaPreviewConfig: RustMediaPreviewConfig?) {
            if (mediaPreviewConfig != null) {
                trySend(mediaPreviewConfig.into())
            }
        }
    })
}

private fun MediaPreviewValue.into(): MediaPreviews {
    return when (this) {
        MediaPreviewValue.On -> MediaPreviews.ON
        MediaPreviewValue.Off -> MediaPreviews.OFF
        MediaPreviewValue.Private -> MediaPreviews.PRIVATE
    }
}

private fun MediaPreviews?.into(): MediaPreviewValue {
    return when (this) {
        null -> MediaPreviewValue.DEFAULT
        MediaPreviews.ON -> MediaPreviewValue.On
        MediaPreviews.OFF -> MediaPreviewValue.Off
        MediaPreviews.PRIVATE -> MediaPreviewValue.Private
    }
}
