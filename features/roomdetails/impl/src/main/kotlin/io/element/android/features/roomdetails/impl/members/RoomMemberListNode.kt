/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.roomdetails.impl.members

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.lifecycle.subscribe
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedInject
import uk.fathertkt.prism.features.analytics.plan.MobileScreen
import io.prism.android.annotations.ContributesNode
import io.prism.android.features.roommembermoderation.api.ModerationAction
import io.prism.android.features.roommembermoderation.api.RoomMemberModerationEvents
import io.prism.android.features.roommembermoderation.api.RoomMemberModerationRenderer
import io.prism.android.libraries.architecture.appyx.launchMolecule
import io.prism.android.libraries.architecture.callback
import io.prism.android.libraries.di.RoomScope
import io.prism.android.libraries.prism.api.core.UserId
import io.prism.android.services.analytics.api.AnalyticsService

@ContributesNode(RoomScope::class)
@AssistedInject
class RoomMemberListNode(
    @Assisted buildContext: BuildContext,
    @Assisted plugins: List<Plugin>,
    private val presenter: RoomMemberListPresenter,
    private val analyticsService: AnalyticsService,
    private val roomMemberModerationRenderer: RoomMemberModerationRenderer,
) : Node(buildContext, plugins = plugins), RoomMemberListNavigator {
    interface Callback : Plugin {
        fun navigateToRoomMemberDetails(roomMemberId: UserId)
        fun navigateToInviteMembers()
    }

    private val callback: Callback = callback()
    private val stateFlow = launchMolecule { presenter.present() }

    init {
        lifecycle.subscribe(
            onResume = {
                analyticsService.screen(MobileScreen(screenName = MobileScreen.ScreenName.RoomMembers))
            }
        )
    }

    override fun openRoomMemberDetails(roomMemberId: UserId) {
        callback.navigateToRoomMemberDetails(roomMemberId)
    }

    override fun openInviteMembers() {
        callback.navigateToInviteMembers()
    }

    override fun exitRoomMemberList() {
        navigateUp()
    }

    @Composable
    override fun View(modifier: Modifier) {
        val state by stateFlow.collectAsState()
        RoomMemberListView(
            state = state,
            modifier = modifier,
            navigator = this,
        )
        roomMemberModerationRenderer.Render(
            state = state.moderationState,
            onSelectAction = { action, target ->
                when (action) {
                    is ModerationAction.DisplayProfile -> openRoomMemberDetails(target.userId)
                    else -> state.moderationState.eventSink(RoomMemberModerationEvents.ProcessAction(action, target))
                }
            },
            modifier = Modifier,
        )
    }
}

interface RoomMemberListNavigator {
    fun exitRoomMemberList() {}
    fun openRoomMemberDetails(roomMemberId: UserId) {}
    fun openInviteMembers() {}
}
