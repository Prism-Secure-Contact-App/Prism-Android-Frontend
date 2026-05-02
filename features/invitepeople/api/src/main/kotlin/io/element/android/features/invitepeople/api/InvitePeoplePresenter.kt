/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.invitepeople.api

import io.prism.android.libraries.architecture.Presenter
import io.prism.android.libraries.matrix.api.core.RoomId
import io.prism.android.libraries.matrix.api.room.JoinedRoom

interface InvitePeoplePresenter : Presenter<InvitePeopleState> {
    interface Factory {
        fun create(
            joinedRoom: JoinedRoom?,
            roomId: RoomId,
        ): InvitePeoplePresenter
    }
}
