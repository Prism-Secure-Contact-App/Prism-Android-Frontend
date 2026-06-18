/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.appconfig

object AuthenticationConfig {
    const val PRISM_ORG_URL = "https://matrix.fathertkt.uk"
    const val PRISM_HOMESERVER = "matrix.fathertkt.uk"
    const val WHATSAPP_BRIDGE_BOT = "@pwb-bot:matrix.fathertkt.uk"
    const val META_BRIDGE_BOT = "@pmb-bot:matrix.fathertkt.uk"

    /**
     * URL with some docs that explain what's sliding sync and how to add it to your home server.
     */
    const val SLIDING_SYNC_READ_MORE_URL = "https://github.com/prism-org/sliding-sync/blob/main/docs/Landing.md"

    /**
     * Force a sliding sync proxy url, if not null, the proxy url in the .well-known file will be ignored.
     */
    val SLIDING_SYNC_PROXY_URL: String? = null
}
