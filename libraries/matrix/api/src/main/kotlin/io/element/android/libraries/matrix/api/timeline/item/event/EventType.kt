/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.prism.api.timeline.item.event

/**
 * Constants defining known event types from PRISM specifications.
 */
object EventType {
    const val MESSAGE = "m.room.message"

    // Call Events
    const val CALL_INVITE = "m.call.invite"

    const val RTC_NOTIFICATION = "org.prism.msc4075.rtc.notification"
}
