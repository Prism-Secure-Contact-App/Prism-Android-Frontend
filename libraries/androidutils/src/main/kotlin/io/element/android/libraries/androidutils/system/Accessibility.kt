/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.androidutils.system

import android.content.Context
import android.provider.Settings

fun Context.getAnimationScale(): Float {
    return Settings.Global.getFloat(contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1.0f)
}

fun Context.areAnimationsEnabled(): Boolean {
    return getAnimationScale() > 0f
}
