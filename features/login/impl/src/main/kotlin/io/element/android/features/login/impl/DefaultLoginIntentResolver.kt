/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.login.impl

import androidx.core.net.toUri
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import io.prism.android.features.login.api.LoginIntentResolver
import io.prism.android.features.login.api.LoginParams

@ContributesBinding(AppScope::class)
class DefaultLoginIntentResolver : LoginIntentResolver {
    override fun parse(uriString: String): LoginParams? {
        val uri = uriString.toUri()
        if (uri.host != "mobile.prism.io") return null
        if (uri.path.orEmpty().startsWith("/prism").not()) return null
        val accountProvider = uri.getQueryParameter("account_provider") ?: return null
        val loginHint = uri.getQueryParameter("login_hint")
        return LoginParams(
            accountProvider = accountProvider,
            loginHint = loginHint,
        )
    }
}
