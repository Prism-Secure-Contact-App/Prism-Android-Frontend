/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.core.extensions

import io.prism.android.libraries.core.meta.BuildMeta
import io.prism.android.libraries.core.meta.BuildType

fun BuildMeta.isPRISM(): Boolean {
    return when (buildType) {
        BuildType.RELEASE -> applicationId == "io.prism.android.x"
        BuildType.NIGHTLY -> applicationId == "io.prism.android.x.nightly"
        BuildType.DEBUG -> applicationId == "io.prism.android.x.debug"
    }
}
