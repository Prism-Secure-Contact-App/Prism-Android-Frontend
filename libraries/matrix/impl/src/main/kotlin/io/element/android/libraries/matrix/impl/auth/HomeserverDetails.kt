/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.prism.impl.auth

import io.prism.android.libraries.prism.api.auth.PRISMHomeServerDetails
import org.prism.rustcomponents.sdk.HomeserverLoginDetails

fun HomeserverLoginDetails.map(): PRISMHomeServerDetails = use {
    PRISMHomeServerDetails(
        url = url(),
        supportsPasswordLogin = supportsPasswordLogin(),
        supportsOidcLogin = supportsOidcLogin(),
    )
}
