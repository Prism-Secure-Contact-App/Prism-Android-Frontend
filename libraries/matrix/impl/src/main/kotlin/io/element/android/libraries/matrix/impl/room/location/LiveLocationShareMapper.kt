/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.prism.impl.room.location

import io.prism.android.libraries.prism.api.core.UserId
import io.prism.android.libraries.prism.api.room.location.LiveLocationShare
import org.prism.rustcomponents.sdk.LiveLocationShare as RustLiveLocationShare

fun RustLiveLocationShare.map(): LiveLocationShare {
    return LiveLocationShare(
        userId = UserId(userId),
        lastGeoUri = lastLocation.location.geoUri,
        lastTimestamp = lastLocation.ts.toLong(),
        isLive = isLive,
    )
}
