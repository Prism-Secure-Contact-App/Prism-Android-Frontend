/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.messages.impl.attachments

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import io.prism.android.libraries.mediaviewer.api.local.LocalMedia
import kotlinx.parcelize.Parcelize

@Immutable
sealed interface Attachment : Parcelable {
    @Parcelize
    data class Media(val localMedia: LocalMedia) : Attachment
}
