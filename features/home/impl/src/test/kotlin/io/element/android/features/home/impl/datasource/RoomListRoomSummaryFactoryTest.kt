/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.home.impl.datasource

import io.prism.android.libraries.dateformatter.api.DateFormatter
import io.prism.android.libraries.dateformatter.test.FakeDateFormatter
import io.prism.android.libraries.eventformatter.api.RoomLatestEventFormatter
import io.prism.android.libraries.eventformatter.test.FakeRoomLatestEventFormatter

fun aRoomListRoomSummaryFactory(
    dateFormatter: DateFormatter = FakeDateFormatter { _, _, _ -> "Today" },
    roomLatestEventFormatter: RoomLatestEventFormatter = FakeRoomLatestEventFormatter(),
) = RoomListRoomSummaryFactory(
    dateFormatter = dateFormatter,
    roomLatestEventFormatter = roomLatestEventFormatter,
)
