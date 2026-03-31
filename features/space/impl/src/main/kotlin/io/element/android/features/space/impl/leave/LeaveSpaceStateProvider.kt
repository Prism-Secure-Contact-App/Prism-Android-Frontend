/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.space.impl.leave

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.prism.android.libraries.architecture.AsyncAction
import io.prism.android.libraries.architecture.AsyncData
import io.prism.android.libraries.prism.api.room.join.JoinRule
import io.prism.android.libraries.prism.api.spaces.SpaceRoom
import io.prism.android.libraries.previewutils.room.aSpaceRoom
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

class LeaveSpaceStateProvider : PreviewParameterProvider<LeaveSpaceState> {
    override val values: Sequence<LeaveSpaceState>
        get() = sequenceOf(
            aLeaveSpaceState(),
            aLeaveSpaceState(
                spaceName = null,
                selectableSpaceRooms = AsyncData.Success(persistentListOf()),
            ),
            aLeaveSpaceState(
                selectableSpaceRooms = AsyncData.Success(
                    persistentListOf(
                        aSelectableSpaceRoom(
                            spaceRoom = aSpaceRoom(
                                displayName = "A long space name that should be truncated",
                                worldReadable = true,
                            ),
                            isLastOwner = true,
                        ),
                        aSelectableSpaceRoom(
                            spaceRoom = aSpaceRoom(
                                joinRule = JoinRule.Invite,
                            ),
                            isSelected = false,
                        ),
                    )
                )
            ),
            aLeaveSpaceState(
                selectableSpaceRooms = AsyncData.Success(
                    persistentListOf(
                        aSelectableSpaceRoom(
                            spaceRoom = aSpaceRoom(
                                worldReadable = true,
                            ),
                            isLastOwner = true,
                        ),
                        aSelectableSpaceRoom(
                            spaceRoom = aSpaceRoom(
                                joinRule = JoinRule.Invite,
                            ),
                            isSelected = true,
                        ),
                    )
                )
            ),
            aLeaveSpaceState(
                selectableSpaceRooms = AsyncData.Success(
                    persistentListOf(
                        aSelectableSpaceRoom(
                            spaceRoom = aSpaceRoom(
                                worldReadable = true,
                            ),
                            isLastOwner = true,
                        ),
                    )
                ),
            ),
            aLeaveSpaceState(
                selectableSpaceRooms = AsyncData.Success(
                    persistentListOf(
                        aSelectableSpaceRoom(
                            spaceRoom = aSpaceRoom(
                                worldReadable = true,
                            ),
                            isLastOwner = true,
                        ),
                        aSelectableSpaceRoom(
                            spaceRoom = aSpaceRoom(),
                            isLastOwner = true,
                        ),
                    )
                ),
            ),
            aLeaveSpaceState(
                selectableSpaceRooms = AsyncData.Success(
                    List(10) { aSelectableSpaceRoom() }.toImmutableList()
                ),
                leaveSpaceAction = AsyncAction.Loading,
            ),
            aLeaveSpaceState(
                selectableSpaceRooms = AsyncData.Success(
                    List(10) { aSelectableSpaceRoom() }.toImmutableList()
                ),
                leaveSpaceAction = AsyncAction.Failure(Exception("An error")),
            ),
            aLeaveSpaceState(
                selectableSpaceRooms = AsyncData.Failure(Exception("An error")),
            ),
            aLeaveSpaceState(
                isLastOwner = true,
            ),
            aLeaveSpaceState(
                isLastOwner = true,
                areCreatorsPrivileged = true,
            ),
        )
}

fun aLeaveSpaceState(
    spaceName: String? = "Space name",
    isLastOwner: Boolean = false,
    areCreatorsPrivileged: Boolean = false,
    selectableSpaceRooms: AsyncData<ImmutableList<SelectableSpaceRoom>> = AsyncData.Uninitialized,
    leaveSpaceAction: AsyncAction<Unit> = AsyncAction.Uninitialized,
) = LeaveSpaceState(
    spaceName = spaceName,
    needsOwnerChange = isLastOwner,
    areCreatorsPrivileged = areCreatorsPrivileged,
    selectableSpaceRooms = selectableSpaceRooms,
    leaveSpaceAction = leaveSpaceAction,
    eventSink = { }
)

fun aSelectableSpaceRoom(
    spaceRoom: SpaceRoom = aSpaceRoom(),
    isLastOwner: Boolean = false,
    joinedMembersCount: Int = 2,
    isSelected: Boolean = false,
) = SelectableSpaceRoom(
    spaceRoom = spaceRoom,
    isLastOwner = isLastOwner,
    joinedMembersCount = joinedMembersCount,
    isSelected = isSelected,
)
