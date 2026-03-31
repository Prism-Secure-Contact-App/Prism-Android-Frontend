/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.login.impl.accountprovider

data class AccountProvider(
    val url: String,
    val title: String = url.removePrefix("https://").removePrefix("http://"),
    val subtitle: String? = null,
    val isPublic: Boolean = false,
    val isPRISMOrg: Boolean = false,
)
