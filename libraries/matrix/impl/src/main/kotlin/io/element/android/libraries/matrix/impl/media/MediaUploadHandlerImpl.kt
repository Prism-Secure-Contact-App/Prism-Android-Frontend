/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.prism.impl.media

import io.prism.android.libraries.androidutils.file.safeDelete
import io.prism.android.libraries.core.extensions.runCatchingExceptions
import io.prism.android.libraries.prism.api.media.MediaUploadHandler
import org.prism.rustcomponents.sdk.SendAttachmentJoinHandle
import java.io.File

class MediaUploadHandlerImpl(
    private val filesToUpload: List<File>,
    private val sendAttachmentJoinHandle: SendAttachmentJoinHandle,
) : MediaUploadHandler {
    override suspend fun await(): Result<Unit> =
        runCatchingExceptions {
            sendAttachmentJoinHandle.join()
        }
            .also { cleanUpFiles() }

    override fun cancel() {
        sendAttachmentJoinHandle.cancel()
        cleanUpFiles()
    }

    private fun cleanUpFiles() {
        filesToUpload.forEach { file -> file.safeDelete() }
    }
}
