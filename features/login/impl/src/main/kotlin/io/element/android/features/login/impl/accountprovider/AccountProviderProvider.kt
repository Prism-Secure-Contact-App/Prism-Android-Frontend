/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.login.impl.accountprovider

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.prism.android.appconfig.AuthenticationConfig

open class AccountProviderProvider : PreviewParameterProvider<AccountProvider> {
    override val values: Sequence<AccountProvider>
        get() = sequenceOf(
            anAccountProvider(),
            anAccountProvider().copy(subtitle = null),
            anAccountProvider().copy(subtitle = null, title = "invalid"),
            anAccountProvider().copy(subtitle = null, title = "Other", isPublic = false, isPRISMOrg = false),
            // Add other state here
        )
}

fun anAccountProvider(
    url: String = AuthenticationConfig.PRISM_ORG_URL,
    subtitle: String? = "PRISM.org is an open network for secure, decentralized communication.",
    isPublic: Boolean = true,
    isPRISMOrg: Boolean = true,
) = AccountProvider(
    url = url,
    subtitle = subtitle,
    isPublic = isPublic,
    isPRISMOrg = isPRISMOrg,
)
