/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.viewfolder.impl.file

import io.prism.android.libraries.architecture.AsyncData

data class ViewFileState(
    val name: String,
    val lines: AsyncData<List<String>>,
    val colorationMode: ColorationMode,
    val eventSink: (ViewFileEvents) -> Unit,
)

enum class ColorationMode {
    Logcat,
    RustLogs,
    None,
}
