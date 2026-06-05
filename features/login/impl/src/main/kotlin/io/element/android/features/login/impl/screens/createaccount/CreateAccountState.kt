/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.login.impl.screens.createaccount

import io.prism.android.libraries.architecture.AsyncAction
import io.prism.android.libraries.matrix.api.core.SessionId

data class PasswordRequirement(
    val label: String,
    val satisfied: Boolean,
)

data class CreateAccountState(
    val url: String,
    val username: String = "",
    val password: String = "",
    val passwordConfirm: String = "",
    val isSubmitEnabled: Boolean = false,
    val pageProgress: Int,
    val createAction: AsyncAction<SessionId>,
    val isDebugBuild: Boolean,
    val passwordRequirements: List<PasswordRequirement> = emptyList(),
    val eventSink: (CreateAccountEvents) -> Unit
)
