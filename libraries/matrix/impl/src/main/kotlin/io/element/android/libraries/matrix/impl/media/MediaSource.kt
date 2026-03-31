/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.prism.impl.media

import io.prism.android.libraries.prism.api.media.MediaSource
import org.prism.rustcomponents.sdk.use
import org.prism.rustcomponents.sdk.MediaSource as RustMediaSource

fun RustMediaSource.map(): MediaSource = use {
    MediaSource(it.url(), it.toJson())
}
