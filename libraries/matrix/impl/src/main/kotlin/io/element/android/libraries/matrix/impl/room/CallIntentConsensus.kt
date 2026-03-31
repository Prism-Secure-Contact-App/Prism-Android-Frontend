/*
 * Copyright (c) 2026 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.prism.impl.room

import io.prism.android.libraries.prism.api.notification.CallIntent
import io.prism.android.libraries.prism.api.room.CallIntentConsensus
import org.prism.rustcomponents.sdk.RtcCallIntent
import org.prism.rustcomponents.sdk.RtcCallIntentConsensus

fun RtcCallIntentConsensus.map(): CallIntentConsensus = when (this) {
    is RtcCallIntentConsensus.Full -> CallIntentConsensus.Full(v1.map())
    is RtcCallIntentConsensus.Partial -> CallIntentConsensus.Partial(
        callIntent = intent.map(),
        agreeingCount = agreeingCount.toInt(),
        totalCount = totalCount.toInt()
    )
    RtcCallIntentConsensus.None -> CallIntentConsensus.None
}

fun RtcCallIntent.map(): CallIntent = when (this) {
    RtcCallIntent.VIDEO -> CallIntent.VIDEO
    RtcCallIntent.AUDIO -> CallIntent.AUDIO
}
