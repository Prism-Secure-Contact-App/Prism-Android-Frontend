/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.eventformatter.test

import io.prism.android.libraries.eventformatter.api.RoomLatestEventFormatter
import io.prism.android.libraries.prism.api.roomlist.LatestEventValue

class FakeRoomLatestEventFormatter : RoomLatestEventFormatter {
    private var result: CharSequence? = null

    override fun format(latestEvent: LatestEventValue.Local, isDmRoom: Boolean): CharSequence? {
        return result
    }

    override fun format(latestEvent: LatestEventValue.Remote, isDmRoom: Boolean): CharSequence? {
        return result
    }

    fun givenFormatResult(result: CharSequence?) {
        this.result = result
    }
}
