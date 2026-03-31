/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.messages.test.attachments.video

import io.prism.android.features.messages.impl.attachments.video.MediaOptimizationSelectorPresenter
import io.prism.android.features.messages.impl.attachments.video.MediaOptimizationSelectorState
import io.prism.android.libraries.architecture.AsyncData
import io.prism.android.libraries.mediaviewer.api.local.LocalMedia

class FakeMediaOptimizationSelectorPresenterFactory(
    private val fakePresenter: MediaOptimizationSelectorPresenter = MediaOptimizationSelectorPresenter {
        MediaOptimizationSelectorState(
            maxUploadSize = AsyncData.Uninitialized,
            videoSizeEstimations = AsyncData.Uninitialized,
            isImageOptimizationEnabled = null,
            selectedVideoPreset = null,
            displayMediaSelectorViews = null,
            displayVideoPresetSelectorDialog = false,
            eventSink = {},
        )
    }
) : MediaOptimizationSelectorPresenter.Factory {
    override fun create(localMedia: LocalMedia): MediaOptimizationSelectorPresenter {
        return fakePresenter
    }
}
