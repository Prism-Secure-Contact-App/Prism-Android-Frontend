/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.eventformatter.api

import io.prism.android.libraries.matrix.api.roomlist.LatestEventValue

interface RoomLatestEventFormatter {
    fun format(latestEvent: LatestEventValue.Local, isDmRoom: Boolean): CharSequence?
    fun format(latestEvent: LatestEventValue.Remote, isDmRoom: Boolean): CharSequence?
}
