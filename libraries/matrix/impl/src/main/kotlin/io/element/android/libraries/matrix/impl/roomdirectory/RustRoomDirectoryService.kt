/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.prism.impl.roomdirectory

import io.prism.android.libraries.prism.api.roomdirectory.RoomDirectoryList
import io.prism.android.libraries.prism.api.roomdirectory.RoomDirectoryService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import org.prism.rustcomponents.sdk.Client

class RustRoomDirectoryService(
    private val client: Client,
    private val sessionDispatcher: CoroutineDispatcher,
) : RoomDirectoryService {
    override fun createRoomDirectoryList(scope: CoroutineScope): RoomDirectoryList {
        return RustRoomDirectoryList(client.roomDirectorySearch(), scope, sessionDispatcher)
    }
}
