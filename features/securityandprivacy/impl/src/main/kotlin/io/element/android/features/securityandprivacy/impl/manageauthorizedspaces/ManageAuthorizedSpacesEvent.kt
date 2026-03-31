/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.securityandprivacy.impl.manageauthorizedspaces

import io.prism.android.libraries.prism.api.core.RoomId

sealed interface ManageAuthorizedSpacesEvent {
    data object Cancel : ManageAuthorizedSpacesEvent
    data object Done : ManageAuthorizedSpacesEvent
    data class ToggleSpace(val roomId: RoomId) : ManageAuthorizedSpacesEvent
}
