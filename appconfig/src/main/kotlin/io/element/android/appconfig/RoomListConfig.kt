/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.appconfig

object RoomListConfig {
    const val SHOW_INVITE_MENU_ITEM = true
    const val SHOW_REPORT_PROBLEM_MENU_ITEM = true
    const val SHOW_WALLET_MENU_ITEM = true

    const val HAS_DROP_DOWN_MENU = SHOW_INVITE_MENU_ITEM || SHOW_REPORT_PROBLEM_MENU_ITEM || SHOW_WALLET_MENU_ITEM
}
