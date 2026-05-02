/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.securityandprivacy.impl.editroomaddress

import io.prism.android.libraries.architecture.AsyncAction
import io.prism.android.libraries.matrix.ui.room.address.RoomAddressValidity

data class EditRoomAddressState(
    val homeserverName: String,
    val roomAddress: String,
    val roomAddressValidity: RoomAddressValidity,
    val saveAction: AsyncAction<Unit>,
    val eventSink: (EditRoomAddressEvents) -> Unit
) {
    val canBeSaved = roomAddressValidity == RoomAddressValidity.Valid
}
