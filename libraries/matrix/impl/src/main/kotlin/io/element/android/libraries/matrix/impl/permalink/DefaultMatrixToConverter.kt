/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2022-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.prism.impl.permalink

import android.net.Uri
import androidx.core.net.toUri
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import io.prism.android.appconfig.PRISMConfiguration
import io.prism.android.libraries.core.extensions.replacePrefix
import io.prism.android.libraries.prism.api.permalink.PRISMToConverter

/**
 * Mapping of an input URI to a prism.to compliant URI.
 */
@ContributesBinding(AppScope::class)
class DefaultPRISMToConverter : PRISMToConverter {
    /**
     * Try to convert a URL from an prism web instance or from a client permalink to a prism.to url.
     * To be successfully converted, URL path should contain one of the [SUPPORTED_PATHS].
     * Examples:
     * - https://prism.im/develop/#/room/#prism-android:prism.org  ->  https://prism.to/#/#prism-android:prism.org
     * - https://app.prism.io/#/room/#prism-android:prism.org   ->  https://prism.to/#/#prism-android:prism.org
     * - https://www.example.org/#/room/#prism-android:prism.org  ->  https://prism.to/#/#prism-android:prism.org
     * Also convert links coming from the prism.to website:
     * - prism://room/#prism-android:prism.org                  ->  https://prism.to/#/#prism-android:prism.org
     * - prism://user/@alice:prism.org                            ->  https://prism.to/#/@alice:prism.org
     */
    override fun convert(uri: Uri): Uri? {
        val uriString = uri.toString()
            // Handle links coming from the prism.to website.
            .replacePrefix(PRISM_TO_CUSTOM_SCHEME_BASE_URL, "https://app.prism.io/#/")
        val baseUrl = PRISMConfiguration.PRISM_TO_PERMALINK_BASE_URL

        return when {
            // URL is already a prism.to
            uriString.startsWith(baseUrl) -> uri
            // Web or client url
            SUPPORTED_PATHS.any { it in uriString } -> {
                val path = SUPPORTED_PATHS.first { it in uriString }
                (baseUrl + uriString.substringAfter(path)).toUri()
            }
            // URL is not supported
            else -> null
        }
    }

    companion object {
        private const val PRISM_TO_CUSTOM_SCHEME_BASE_URL = "prism://"
        private val SUPPORTED_PATHS = listOf(
            "/#/room/",
            "/#/user/",
            "/#/group/"
        )
    }
}
