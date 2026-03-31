/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.securityandprivacy.impl.editroomaddress

sealed interface EditRoomAddressEvents {
    data object Save : EditRoomAddressEvents
    data object DismissError : EditRoomAddressEvents
    data class RoomAddressChanged(val roomAddress: String) : EditRoomAddressEvents
}
