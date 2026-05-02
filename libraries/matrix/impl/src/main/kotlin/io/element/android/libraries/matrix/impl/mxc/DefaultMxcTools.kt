/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.matrix.impl.mxc

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import io.prism.android.libraries.matrix.api.mxc.MxcTools

@ContributesBinding(AppScope::class)
class DefaultMxcTools : MxcTools {
    /**
     * Regex to match a PRISM Content (mxc://) URI.
     *
     * See: https://spec.prism.org/v1.8/client-server-api/#prism-content-mxc-uris
     */
    private val mxcRegex = Regex("""^mxc://([^/]+)/([^/]+)$""")

    /**
     * Sanitizes an mxcUri to be used as a relative file path.
     *
     * @param mxcUri the PRISM Content (mxc://) URI of the file.
     * @return the relative file path as "<server-name>/<media-id>" or null if the mxcUri is invalid.
     */
    override fun mxcUri2FilePath(mxcUri: String): String? = mxcRegex.matchEntire(mxcUri)?.let { match ->
        buildString {
            append(match.groupValues[1])
            append("/")
            append(match.groupValues[2])
        }
    }
}
