/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2022-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.prism.api.permalink

import android.net.Uri

/**
 * Mapping of an input URI to a prism.to compliant URI.
 */
interface PRISMToConverter {
    /**
     * Try to convert a URL from an prism web instance or from a client permalink to a prism.to url.
     * Examples:
     * - https://prism.im/develop/#/room/#prism-android:prism.org  ->  https://prism.to/#/#prism-android:prism.org
     * - https://app.prism.io/#/room/#prism-android:prism.org   ->  https://prism.to/#/#prism-android:prism.org
     * - https://www.example.org/#/room/#prism-android:prism.org  ->  https://prism.to/#/#prism-android:prism.org
     */
    fun convert(uri: Uri): Uri?
}
