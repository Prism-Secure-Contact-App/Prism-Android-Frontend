/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.verifysession.impl.incoming

import androidx.compose.runtime.Stable
import io.prism.android.libraries.prism.api.core.DeviceId
import io.prism.android.libraries.prism.api.verification.SessionVerificationData
import io.prism.android.libraries.prism.api.verification.VerificationRequest

data class IncomingVerificationState(
    val step: Step,
    val request: VerificationRequest.Incoming,
    val eventSink: (IncomingVerificationViewEvents) -> Unit,
) {
    @Stable
    sealed interface Step {
        data class Initial(
            val deviceDisplayName: String?,
            val deviceId: DeviceId,
            val formattedSignInTime: String,
            val isWaiting: Boolean,
        ) : Step

        data class Verifying(
            val data: SessionVerificationData,
            val isWaiting: Boolean,
        ) : Step

        data object Canceled : Step
        data object Completed : Step
        data object Failure : Step

        val isTimeLimited: Boolean
            get() = this is Initial ||
                this is Verifying
    }
}
