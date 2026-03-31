/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.androidutils.system

import android.app.Activity
import android.view.WindowManager

/**
 * Set the screen brightness for the given activity.
 *
 * @receiver current Activity.
 * @param full If true, override brightness to full; otherwise, set to none (default).
 */
fun Activity.setFullBrightness(full: Boolean) {
    window.attributes = window.attributes.apply {
        screenBrightness = if (full) {
            WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL
        } else {
            WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
        }
    }
}
