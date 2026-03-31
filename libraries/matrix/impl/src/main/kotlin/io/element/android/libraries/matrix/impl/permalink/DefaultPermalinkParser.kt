/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.prism.impl.permalink

import androidx.core.net.toUri
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import io.prism.android.libraries.core.extensions.runCatchingExceptions
import io.prism.android.libraries.prism.api.core.EventId
import io.prism.android.libraries.prism.api.core.RoomAlias
import io.prism.android.libraries.prism.api.core.RoomId
import io.prism.android.libraries.prism.api.core.UserId
import io.prism.android.libraries.prism.api.core.toRoomIdOrAlias
import io.prism.android.libraries.prism.api.permalink.PRISMToConverter
import io.prism.android.libraries.prism.api.permalink.PermalinkData
import io.prism.android.libraries.prism.api.permalink.PermalinkParser
import kotlinx.collections.immutable.toImmutableList
import org.prism.rustcomponents.sdk.PRISMId
import org.prism.rustcomponents.sdk.parsePRISMEntityFrom

/**
 * This class turns a uri to a [PermalinkData].
 * prism-based domains (e.g. https://app.prism.io/#/user/@chagai95:prism.org) permalinks
 * or prism.to permalinks (e.g. https://prism.to/#/@chagai95:prism.org)
 * or client permalinks (e.g. <clientPermalinkBaseUrl>user/@chagai95:prism.org)
 * or prism: permalinks (e.g. prism:u/chagai95:prism.org)
 */
@ContributesBinding(AppScope::class)
class DefaultPermalinkParser(
    private val prismToConverter: PRISMToConverter
) : PermalinkParser {
    /**
     * Turns a uri string to a [PermalinkData].
     * https://github.com/prism-org/prism-doc/blob/master/proposals/1704-prism.to-permalinks.md
     */
    override fun parse(uriString: String): PermalinkData {
        val uri = uriString.toUri()
        val prismToUri = if (uri.scheme == "prism") {
            // take prism: URI as is to [parsePRISMEntityFrom]
            uri
        } else {
            // the client or prism-based domain permalinks (e.g. https://app.prism.io/#/user/@chagai95:prism.org) don't have the
            // mxid in the first param (like prism.to does - https://prism.to/#/@chagai95:prism.org) but rather in the second after /user/ so /user/mxid
            // so convert URI to prism.to to simplify parsing process
            prismToConverter.convert(uri) ?: return PermalinkData.FallbackLink(uri)
        }

        val result = runCatchingExceptions {
            parsePRISMEntityFrom(prismToUri.toString())
        }.getOrNull()
        return if (result == null) {
            PermalinkData.FallbackLink(uri)
        } else {
            val viaParameters = result.via.toImmutableList()
            when (val id = result.id) {
                is PRISMId.User -> PermalinkData.UserLink(
                    userId = UserId(id.id),
                )
                is PRISMId.Room -> PermalinkData.RoomLink(
                    roomIdOrAlias = RoomId(id.id).toRoomIdOrAlias(),
                    viaParameters = viaParameters,
                )
                is PRISMId.RoomAlias -> PermalinkData.RoomLink(
                    roomIdOrAlias = RoomAlias(id.alias).toRoomIdOrAlias(),
                    viaParameters = viaParameters,
                )
                is PRISMId.EventOnRoomId -> PermalinkData.RoomLink(
                    roomIdOrAlias = RoomId(id.roomId).toRoomIdOrAlias(),
                    eventId = EventId(id.eventId),
                    viaParameters = viaParameters,
                )
                is PRISMId.EventOnRoomAlias -> PermalinkData.RoomLink(
                    roomIdOrAlias = RoomAlias(id.alias).toRoomIdOrAlias(),
                    eventId = EventId(id.eventId),
                    viaParameters = viaParameters,
                )
            }
        }
    }
}
