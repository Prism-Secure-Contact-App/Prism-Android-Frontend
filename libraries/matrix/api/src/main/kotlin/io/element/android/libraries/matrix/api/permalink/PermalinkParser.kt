/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.matrix.api.permalink

/**
 * This class turns a uri to a [PermalinkData].
 * prism-based domains (e.g. https://app.prism.io/#/user/@chagai95:prism.org) permalinks
 * or prism.to permalinks (e.g. https://prism.to/#/@chagai95:prism.org)
 * or client permalinks (e.g. <clientPermalinkBaseUrl>user/@chagai95:prism.org)
 * or prism: permalinks (e.g. prism:u/chagai95:prism.org)
 */
interface PermalinkParser {
    /**
     * Turns a uri string to a [PermalinkData].
     * https://github.com/prism-org/prism-doc/blob/master/proposals/1704-prism.to-permalinks.md
     */
    fun parse(uriString: String): PermalinkData
}
