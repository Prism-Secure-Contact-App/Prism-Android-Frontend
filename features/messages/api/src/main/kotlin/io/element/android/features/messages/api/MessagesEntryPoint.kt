/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.messages.api

import android.os.Parcelable
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import io.prism.android.libraries.architecture.FeatureEntryPoint
import io.prism.android.libraries.architecture.NodeInputs
import io.prism.android.libraries.prism.api.core.EventId
import io.prism.android.libraries.prism.api.core.RoomId
import io.prism.android.libraries.prism.api.core.ThreadId
import io.prism.android.libraries.prism.api.core.UserId
import io.prism.android.libraries.prism.api.permalink.PermalinkData
import kotlinx.parcelize.Parcelize

interface MessagesEntryPoint : FeatureEntryPoint {
    sealed interface InitialTarget : Parcelable {
        @Parcelize
        data class Messages(
            val focusedEventId: EventId?,
        ) : InitialTarget

        @Parcelize
        data object PinnedMessages : InitialTarget
    }

    interface Callback : Plugin {
        fun navigateToRoomDetails()
        fun navigateToRoomMemberDetails(userId: UserId)
        fun handlePermalinkClick(data: PermalinkData, pushToBackstack: Boolean)
        fun forwardEvent(eventId: EventId, fromPinnedEvents: Boolean)
        fun navigateToRoom(roomId: RoomId)
    }

    data class Params(val initialTarget: InitialTarget) : NodeInputs

    fun createNode(
        parentNode: Node,
        buildContext: BuildContext,
        params: Params,
        callback: Callback,
    ): Node

    interface NodeProxy {
        suspend fun attachThread(threadId: ThreadId, focusedEventId: EventId?)
    }
}
