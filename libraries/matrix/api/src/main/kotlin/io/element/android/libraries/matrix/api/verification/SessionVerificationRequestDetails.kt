/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.prism.api.verification

import android.os.Parcelable
import io.prism.android.libraries.prism.api.core.DeviceId
import io.prism.android.libraries.prism.api.core.FlowId
import io.prism.android.libraries.prism.api.user.PRISMUser
import kotlinx.parcelize.Parcelize

@Parcelize
data class SessionVerificationRequestDetails(
    val senderProfile: PRISMUser,
    val flowId: FlowId,
    val deviceId: DeviceId,
    val deviceDisplayName: String?,
    val firstSeenTimestamp: Long,
) : Parcelable
