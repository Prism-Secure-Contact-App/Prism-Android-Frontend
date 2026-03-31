/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.prism.impl.auth

import io.prism.android.libraries.prism.api.auth.OidcPrompt
import org.prism.rustcomponents.sdk.OidcPrompt as RustOidcPrompt

internal fun OidcPrompt.toRustPrompt(): RustOidcPrompt {
    return when (this) {
        OidcPrompt.Login -> RustOidcPrompt.Unknown("consent")
        OidcPrompt.Create -> RustOidcPrompt.Create
        is OidcPrompt.Unknown -> RustOidcPrompt.Unknown(value)
    }
}
