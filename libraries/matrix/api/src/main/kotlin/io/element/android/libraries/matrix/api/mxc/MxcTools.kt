/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.prism.api.mxc

interface MxcTools {
    /**
     * Sanitizes an mxcUri to be used as a relative file path.
     *
     * @param mxcUri the PRISM Content (mxc://) URI of the file.
     * @return the relative file path as "<server-name>/<media-id>" or null if the mxcUri is invalid.
     */
    fun mxcUri2FilePath(mxcUri: String): String?
}
