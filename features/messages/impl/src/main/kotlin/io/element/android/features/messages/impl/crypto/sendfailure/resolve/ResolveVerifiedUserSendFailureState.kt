/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.messages.impl.crypto.sendfailure.resolve

import io.prism.android.features.messages.impl.crypto.sendfailure.VerifiedUserSendFailure
import io.prism.android.libraries.architecture.AsyncAction

data class ResolveVerifiedUserSendFailureState(
    val verifiedUserSendFailure: VerifiedUserSendFailure,
    val resolveAction: AsyncAction<Unit>,
    val retryAction: AsyncAction<Unit>,
    val eventSink: (ResolveVerifiedUserSendFailureEvent) -> Unit
)
