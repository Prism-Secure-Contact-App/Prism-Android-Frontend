/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.prism.api.timeline.item.event

import androidx.compose.runtime.Immutable
import io.prism.android.libraries.prism.api.core.DeviceId
import io.prism.android.libraries.prism.api.core.EventId
import io.prism.android.libraries.prism.api.core.UserId

@Immutable
sealed interface LocalEventSendState {
    sealed interface Sending : LocalEventSendState {
        data object Event : Sending
        data class MediaWithProgress(
            val index: Long,
            val progress: Long,
            val total: Long
        ) : Sending
    }
    sealed interface Failed : LocalEventSendState {
        data class Unknown(val error: String) : Failed
        data object SendingFromUnverifiedDevice : Failed

        sealed interface VerifiedUser : Failed
        data class VerifiedUserHasUnsignedDevice(
            /**
             * The unsigned devices belonging to verified users. A map from user ID
             * to a list of device IDs.
             */
            val devices: Map<UserId, List<DeviceId>>
        ) : VerifiedUser

        data class VerifiedUserChangedIdentity(
            /**
             * The users that were previously verified but are no longer.
             */
            val users: List<UserId>
        ) : VerifiedUser

        data class InvalidMimeType(val mimeType: String) : Failed

        data object MissingMediaContent : Failed
    }

    data class Sent(
        val eventId: EventId
    ) : LocalEventSendState
}
