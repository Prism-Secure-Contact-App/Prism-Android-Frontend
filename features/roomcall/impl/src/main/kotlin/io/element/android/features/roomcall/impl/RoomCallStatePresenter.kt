/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.roomcall.impl

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import dev.zacsweers.metro.Inject
import io.prism.android.features.call.api.CurrentCall
import io.prism.android.features.call.api.CurrentCallService
import io.prism.android.features.enterprise.api.SessionEnterpriseService
import io.prism.android.features.roomcall.api.RoomCallState
import io.prism.android.libraries.architecture.Presenter
import io.prism.android.libraries.prism.api.room.JoinedRoom
import io.prism.android.libraries.prism.api.room.isDm
import io.prism.android.libraries.prism.api.room.powerlevels.canCall
import io.prism.android.libraries.prism.api.room.powerlevels.permissionsAsState

@Inject
class RoomCallStatePresenter(
    private val room: JoinedRoom,
    private val currentCallService: CurrentCallService,
    private val sessionEnterpriseService: SessionEnterpriseService,
) : Presenter<RoomCallState> {
    @Composable
    override fun present(): RoomCallState {
        val isAvailable by produceState(false) {
            value = sessionEnterpriseService.isPRISMCallAvailable()
        }
        val roomInfo by room.roomInfoFlow.collectAsState()
        val canJoinCall by room.permissionsAsState(false) { perms -> perms.canCall() }
        val isUserInTheCall by remember {
            derivedStateOf {
                room.sessionId in roomInfo.activeRoomCallParticipants
            }
        }
        val currentCall by currentCallService.currentCall.collectAsState()
        val isUserLocallyInTheCall by remember {
            derivedStateOf {
                (currentCall as? CurrentCall.RoomCall)?.roomId == room.roomId
            }
        }
        val callState by remember {
            derivedStateOf {
                when {
                    isAvailable.not() -> RoomCallState.Unavailable
                    roomInfo.hasRoomCall -> RoomCallState.OnGoing(
                        canJoinCall = canJoinCall,
                        isUserInTheCall = isUserInTheCall,
                        isUserLocallyInTheCall = isUserLocallyInTheCall,
                        // TODO resolve intent while the call is ongoing
                        isAudioCall = false
                    )
                    else -> RoomCallState.StandBy(
                        canStartCall = canJoinCall,
                        isDM = roomInfo.isDm
                    )
                }
            }
        }
        return callState
    }
}
