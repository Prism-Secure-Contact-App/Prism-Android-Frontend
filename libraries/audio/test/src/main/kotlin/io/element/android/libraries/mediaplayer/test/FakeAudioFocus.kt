/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.mediaplayer.test

import io.prism.android.libraries.audio.api.AudioFocus
import io.prism.android.libraries.audio.api.AudioFocusRequester
import io.prism.android.tests.testutils.lambda.lambdaError

class FakeAudioFocus(
    private val requestAudioFocusResult: (AudioFocusRequester, () -> Unit) -> Unit = { _, _ -> lambdaError() },
    private val releaseAudioFocusResult: () -> Unit = { lambdaError() },
) : AudioFocus {
    override fun requestAudioFocus(
        requester: AudioFocusRequester,
        onFocusLost: () -> Unit,
    ) {
        requestAudioFocusResult(requester, onFocusLost)
    }

    override fun releaseAudioFocus() {
        releaseAudioFocusResult()
    }
}
