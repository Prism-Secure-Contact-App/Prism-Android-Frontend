/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.roomdetails.api

import android.os.Parcelable
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import io.prism.android.libraries.architecture.FeatureEntryPoint
import io.prism.android.libraries.architecture.NodeInputs
import io.prism.android.libraries.prism.api.core.EventId
import io.prism.android.libraries.prism.api.core.RoomId
import io.prism.android.libraries.prism.api.core.UserId
import io.prism.android.libraries.prism.api.permalink.PermalinkData
import kotlinx.parcelize.Parcelize

interface RoomDetailsEntryPoint : FeatureEntryPoint {
    sealed interface InitialTarget : Parcelable {
        @Parcelize
        data object RoomDetails : InitialTarget

        @Parcelize
        data object RoomMemberList : InitialTarget

        @Parcelize
        data class RoomMemberDetails(val roomMemberId: UserId) : InitialTarget

        @Parcelize
        data object RoomNotificationSettings : InitialTarget
    }

    data class Params(val initialPRISM: InitialTarget) : NodeInputs

    interface Callback : Plugin {
        fun navigateToGlobalNotificationSettings()
        fun navigateToRoom(roomId: RoomId, serverNames: List<String>)
        fun handlePermalinkClick(data: PermalinkData, pushToBackstack: Boolean)
        fun startForwardEventFlow(eventId: EventId, fromPinnedEvents: Boolean)
    }

    fun createNode(
        parentNode: Node,
        buildContext: BuildContext,
        params: Params,
        callback: Callback,
    ): Node
}
