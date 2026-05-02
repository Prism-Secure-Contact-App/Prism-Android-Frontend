/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.login.impl.login

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.prism.android.features.login.impl.error.ChangeServerErrorProvider
import io.prism.android.libraries.matrix.api.auth.AuthenticationException

class LoginModeViewErrorProvider : PreviewParameterProvider<Exception> {
    override val values: Sequence<Exception>
        get() = ChangeServerErrorProvider().values +
            AuthenticationException.AccountAlreadyLoggedIn("@alice:prism.org")
}
