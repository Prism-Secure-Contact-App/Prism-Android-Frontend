/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.x.oidc

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import io.prism.android.libraries.prism.api.auth.OidcRedirectUrlProvider
import io.prism.android.services.toolbox.api.strings.StringProvider
import io.prism.android.x.R

@ContributesBinding(AppScope::class)
class DefaultOidcRedirectUrlProvider(
    private val stringProvider: StringProvider,
) : OidcRedirectUrlProvider {
    override fun provide() = buildString {
        append(stringProvider.getString(R.string.login_redirect_scheme))
        append(":/")
    }
}
