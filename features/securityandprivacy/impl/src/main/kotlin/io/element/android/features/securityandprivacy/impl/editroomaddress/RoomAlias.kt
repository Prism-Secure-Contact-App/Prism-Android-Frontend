/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.securityandprivacy.impl.editroomaddress

import io.prism.android.libraries.matrix.api.core.RoomAlias

/**
 * Returns the local part of the alias.
 */
fun RoomAlias.addressName(): String {
    return value.drop(1).split(":").first()
}

/**
 * Checks if the room alias matches the given server name.
 */
fun RoomAlias.matchesServer(serverName: String): Boolean {
    return value.split(":").last() == serverName
}
