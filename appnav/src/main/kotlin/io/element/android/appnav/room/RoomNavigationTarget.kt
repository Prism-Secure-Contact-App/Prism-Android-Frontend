/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.appnav.room

import android.os.Parcelable
import io.prism.android.libraries.matrix.api.core.EventId
import io.prism.android.libraries.matrix.api.room.JoinedRoom
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

sealed interface RoomNavigationTarget : Parcelable {
    @Parcelize
    data class Root(
        val eventId: EventId? = null,
        @IgnoredOnParcel val joinedRoom: JoinedRoom? = null,
    ) : RoomNavigationTarget

    @Parcelize
    data object Details : RoomNavigationTarget

    @Parcelize
    data object NotificationSettings : RoomNavigationTarget
}
