/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.userprofile.api

import io.prism.android.libraries.architecture.Presenter
import io.prism.android.libraries.prism.api.core.UserId

fun interface UserProfilePresenterFactory {
    fun create(userId: UserId): Presenter<UserProfileState>
}
