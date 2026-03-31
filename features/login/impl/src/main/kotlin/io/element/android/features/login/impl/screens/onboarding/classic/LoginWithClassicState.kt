/*
 * Copyright (c) 2026 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.login.impl.screens.onboarding.classic

import io.prism.android.libraries.architecture.AsyncAction

data class LoginWithClassicState(
    val canLoginWithClassic: Boolean,
    val loginWithClassicAction: AsyncAction<Unit>,
    val eventSink: (LoginWithClassicEvent) -> Unit,
)
