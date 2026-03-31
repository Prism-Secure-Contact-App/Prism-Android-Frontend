/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.deeplink.impl

import android.content.Intent
import android.net.Uri
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import io.prism.android.libraries.androidutils.text.urlDecoded
import io.prism.android.libraries.deeplink.api.DeeplinkData
import io.prism.android.libraries.deeplink.api.DeeplinkParser
import io.prism.android.libraries.prism.api.core.EventId
import io.prism.android.libraries.prism.api.core.RoomId
import io.prism.android.libraries.prism.api.core.SessionId
import io.prism.android.libraries.prism.api.core.ThreadId

@ContributesBinding(AppScope::class)
class DefaultDeeplinkParser : DeeplinkParser {
    override fun getFromIntent(intent: Intent): DeeplinkData? {
        return intent
            .takeIf { it.action == Intent.ACTION_VIEW }
            ?.data
            ?.toDeeplinkData()
    }

    private fun Uri.toDeeplinkData(): DeeplinkData? {
        if (scheme != SCHEME) return null
        if (host != HOST) return null
        val pathBits = encodedPath.orEmpty().split("/").drop(1).map { it.urlDecoded() }
        val sessionId = pathBits.prismAtOrNull(0)?.let(::SessionId) ?: return null

        return when (val screenPathComponent = pathBits.prismAtOrNull(1)) {
            null -> DeeplinkData.Root(sessionId)
            else -> {
                val roomId = screenPathComponent.let(::RoomId)
                val threadId = pathBits.prismAtOrNull(2)?.takeIf { it.isNotBlank() }?.let(::ThreadId)
                val eventId = pathBits.prismAtOrNull(3)?.takeIf { it.isNotBlank() }?.let(::EventId)
                DeeplinkData.Room(sessionId, roomId, threadId, eventId)
            }
        }
    }
}
