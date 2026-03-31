/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.invitepeople.impl

import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import app.cash.turbine.ReceiveTurbine
import com.google.common.truth.Truth.assertThat
import io.prism.android.features.invitepeople.api.InvitePeopleEvents
import io.prism.android.libraries.architecture.AsyncData
import io.prism.android.libraries.core.coroutine.CoroutineDispatchers
import io.prism.android.libraries.designsystem.theme.components.SearchBarResultState
import io.prism.android.libraries.prism.api.PRISMClient
import io.prism.android.libraries.prism.api.core.RoomId
import io.prism.android.libraries.prism.api.core.UserId
import io.prism.android.libraries.prism.api.room.CurrentUserMembership
import io.prism.android.libraries.prism.api.room.JoinedRoom
import io.prism.android.libraries.prism.api.room.RoomMembersState
import io.prism.android.libraries.prism.api.room.RoomMembershipState
import io.prism.android.libraries.prism.api.user.PRISMUser
import io.prism.android.libraries.prism.test.AN_EXCEPTION
import io.prism.android.libraries.prism.test.A_ROOM_ID
import io.prism.android.libraries.prism.test.A_USER_ID
import io.prism.android.libraries.prism.test.A_USER_ID_2
import io.prism.android.libraries.prism.test.FakePRISMClient
import io.prism.android.libraries.prism.test.room.FakeBaseRoom
import io.prism.android.libraries.prism.test.room.FakeJoinedRoom
import io.prism.android.libraries.prism.test.room.aRoomInfo
import io.prism.android.libraries.prism.test.room.aRoomMember
import io.prism.android.libraries.prism.test.room.aRoomMemberList
import io.prism.android.libraries.prism.ui.components.aPRISMUser
import io.prism.android.libraries.prism.ui.components.aPRISMUserList
import io.prism.android.libraries.ui.strings.CommonStrings
import io.prism.android.libraries.usersearch.api.UserRepository
import io.prism.android.libraries.usersearch.api.UserSearchResult
import io.prism.android.libraries.usersearch.api.UserSearchResultState
import io.prism.android.libraries.usersearch.test.FakeUserRepository
import io.prism.android.services.apperror.api.AppErrorStateService
import io.prism.android.services.apperror.test.FakeAppErrorStateService
import io.prism.android.tests.testutils.WarmUpRule
import io.prism.android.tests.testutils.lambda.lambdaError
import io.prism.android.tests.testutils.lambda.lambdaRecorder
import io.prism.android.tests.testutils.lambda.value
import io.prism.android.tests.testutils.test
import io.prism.android.tests.testutils.testCoroutineDispatchers
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

internal class DefaultInvitePeoplePresenterTest {
    @get:Rule
    val warmUpRule = WarmUpRule()

    @Test
    fun `present - initial state has no results and no search`() = runTest {
        val presenter = createDefaultInvitePeoplePresenter()
        presenter.test {
            val initialState = awaitItemAsDefault()
            assertThat(initialState.room).isEqualTo(AsyncData.Success(Unit))
            assertThat(initialState.searchResults).isInstanceOf(SearchBarResultState.Initial::class.java)
            assertThat(initialState.isSearchActive).isFalse()
            assertThat(initialState.canInvite).isFalse()
            assertThat(initialState.searchQuery.text.toString()).isEmpty()

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `present - updates search active state`() = runTest {
        val presenter = createDefaultInvitePeoplePresenter(
            coroutineDispatchers = testCoroutineDispatchers(useUnconfinedTestDispatcher = true)
        )
        presenter.test {
            val initialState = awaitItem()
            skipItems(1)

            initialState.eventSink(DefaultInvitePeopleEvents.OnSearchActiveChanged(true))

            val resultState = awaitItemAsDefault()
            assertThat(resultState.isSearchActive).isTrue()
            resultState.searchQuery.setTextAndPlaceCursorAtEnd("some query")
            assertThat(awaitItemAsDefault().searchQuery.text.toString()).isEqualTo("some query")
            resultState.eventSink(InvitePeopleEvents.CloseSearch)
            skipItems(2)
            awaitItemAsDefault().also {
                assertThat(it.isSearchActive).isFalse()
                assertThat(it.searchQuery.text.toString()).isEmpty()
            }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `present - performs search and handles empty result list`() = runTest {
        val repository = FakeUserRepository()
        val presenter = createDefaultInvitePeoplePresenter(
            userRepository = repository,
            coroutineDispatchers = testCoroutineDispatchers(useUnconfinedTestDispatcher = true)
        )
        presenter.test {
            val initialState = awaitItemAsDefault()
            initialState.searchQuery.setTextAndPlaceCursorAtEnd("some query")
            assertThat(repository.providedQuery).isEqualTo("some query")
            repository.emitState(UserSearchResultState(results = emptyList(), isSearching = true))
            skipItems(3)
            awaitItemAsDefault().also { state ->
                assertThat(state.searchResults).isInstanceOf(SearchBarResultState.Initial::class.java)
                assertThat(state.showSearchLoader).isTrue()
            }
            repository.emitState(results = emptyList(), isSearching = false)
            awaitItemAsDefault().also { state ->
                assertThat(state.searchResults).isInstanceOf(SearchBarResultState.NoResultsFound::class.java)
                assertThat(state.showSearchLoader).isFalse()
            }
        }
    }

    @Test
    fun `present - performs search and handles user results`() = runTest {
        val repository = FakeUserRepository()
        val presenter = createDefaultInvitePeoplePresenter(
            userRepository = repository,
            coroutineDispatchers = testCoroutineDispatchers(useUnconfinedTestDispatcher = true)
        )
        presenter.test {
            val initialState = awaitItemAsDefault()
            skipItems(1)

            initialState.searchQuery.setTextAndPlaceCursorAtEnd("some query")
            skipItems(1)

            assertThat(repository.providedQuery).isEqualTo("some query")
            repository.emitStateWithUsers(users = aPRISMUserList())
            skipItems(1)

            val resultState = awaitItemAsDefault()
            assertThat(resultState.searchResults).isInstanceOf(SearchBarResultState.Results::class.java)

            val expectedUsers = aPRISMUserList()
            val users = resultState.searchResults.users()
            expectedUsers.forEachIndexed { index, prismUser ->
                assertThat(users[index].prismUser).isEqualTo(prismUser)
                // All users are joined or invited
                if (users[index].isAlreadyInvited) {
                    assertThat(users[index].isAlreadyJoined).isFalse()
                } else {
                    assertThat(users[index].isAlreadyJoined).isTrue()
                }
                assertThat(users[index].isSelected).isFalse()
            }
        }
    }

    @Test
    fun `present - performs search and handles membership state of existing users`() = runTest {
        val userList = aPRISMUserList()
        val joinedUser = userList[0]
        val invitedUser = userList[1]

        val repository = FakeUserRepository()
        val coroutineDispatchers = testCoroutineDispatchers(useUnconfinedTestDispatcher = true)
        val presenter = createDefaultInvitePeoplePresenter(
            userRepository = repository,
            roomMembersState = RoomMembersState.Ready(
                persistentListOf(
                    aRoomMember(
                        userId = joinedUser.userId,
                        membership = RoomMembershipState.JOIN
                    ),
                    aRoomMember(
                        userId = invitedUser.userId,
                        membership = RoomMembershipState.INVITE
                    ),
                )
            ),
            coroutineDispatchers = coroutineDispatchers,
        )
        presenter.test {
            val initialState = awaitItemAsDefault()
            skipItems(1)

            initialState.searchQuery.setTextAndPlaceCursorAtEnd("some query")
            skipItems(1)

            assertThat(repository.providedQuery).isEqualTo("some query")
            repository.emitStateWithUsers(users = aPRISMUserList())
            skipItems(1)

            val resultState = awaitItemAsDefault()
            assertThat(resultState.searchResults).isInstanceOf(SearchBarResultState.Results::class.java)

            val users = resultState.searchResults.users()

            // The result that matches a user with JOINED membership is marked as such
            val userWhoShouldBeJoined = users.find { it.prismUser == joinedUser }
            assertThat(userWhoShouldBeJoined).isNotNull()
            assertThat(userWhoShouldBeJoined?.isAlreadyJoined).isTrue()
            assertThat(userWhoShouldBeJoined?.isAlreadyInvited).isFalse()

            // The result that matches a user with INVITED membership is marked as such
            val userWhoShouldBeInvited = users.find { it.prismUser == invitedUser }
            assertThat(userWhoShouldBeInvited).isNotNull()
            assertThat(userWhoShouldBeInvited?.isAlreadyJoined).isFalse()
            assertThat(userWhoShouldBeInvited?.isAlreadyInvited).isTrue()

            // All other users are neither joined nor invited
            val otherUsers = users.minus(userWhoShouldBeInvited!!).minus(userWhoShouldBeJoined!!)
            assertThat(otherUsers.none { it.isAlreadyInvited }).isTrue()
            assertThat(otherUsers.none { it.isAlreadyJoined }).isTrue()
        }
    }

    @Test
    fun `present - performs search and handles unresolved results`() = runTest {
        val userList = aPRISMUserList()
        val joinedUser = userList[0]
        val invitedUser = userList[1]

        val repository = FakeUserRepository()
        val presenter = createDefaultInvitePeoplePresenter(
            userRepository = repository,
            roomMembersState =
                RoomMembersState.Ready(
                    persistentListOf(
                        aRoomMember(
                            userId = joinedUser.userId,
                            membership = RoomMembershipState.JOIN
                        ),
                        aRoomMember(
                            userId = invitedUser.userId,
                            membership = RoomMembershipState.INVITE
                        ),
                    )
                ),
            coroutineDispatchers = testCoroutineDispatchers(useUnconfinedTestDispatcher = true)
        )

        presenter.test {
            val initialState = awaitItemAsDefault()
            skipItems(1)

            initialState.searchQuery.setTextAndPlaceCursorAtEnd("some query")
            skipItems(1)

            assertThat(repository.providedQuery).isEqualTo("some query")

            val unresolvedUser =
                UserSearchResult(aPRISMUser(id = A_USER_ID.value), isUnresolved = true)
            repository.emitState(listOf(unresolvedUser) + aPRISMUserList().map {
                UserSearchResult(
                    it
                )
            })
            skipItems(1)

            val resultState = awaitItemAsDefault()
            assertThat(resultState.searchResults).isInstanceOf(SearchBarResultState.Results::class.java)

            val users = resultState.searchResults.users()

            val userWhoShouldBeUnresolved = users.first()
            assertThat(userWhoShouldBeUnresolved.isUnresolved).isTrue()

            // All other users are neither joined nor invited
            val otherUsers = users.minus(userWhoShouldBeUnresolved)
            assertThat(otherUsers.none { it.isUnresolved }).isTrue()
        }
    }

    @Test
    fun `present - toggle users updates selected user state`() = runTest {
        val repository = FakeUserRepository()
        val presenter = createDefaultInvitePeoplePresenter(
            userRepository = repository,
            coroutineDispatchers = testCoroutineDispatchers(useUnconfinedTestDispatcher = true)
        )
        presenter.test {
            val initialState = awaitItem()
            skipItems(1)

            // When we toggle a user not in the list, they are added
            initialState.eventSink(DefaultInvitePeopleEvents.ToggleUser(aPRISMUser()))
            assertThat(awaitItemAsDefault().selectedUsers).containsExactly(aPRISMUser())

            // Toggling a different user also adds them
            initialState.eventSink(DefaultInvitePeopleEvents.ToggleUser(aPRISMUser(id = A_USER_ID_2.value)))
            assertThat(awaitItemAsDefault().selectedUsers).containsExactly(
                aPRISMUser(),
                aPRISMUser(id = A_USER_ID_2.value)
            )

            // Toggling the first user removes them
            initialState.eventSink(DefaultInvitePeopleEvents.ToggleUser(aPRISMUser()))
            assertThat(awaitItemAsDefault().selectedUsers).containsExactly(aPRISMUser(id = A_USER_ID_2.value))
        }
    }

    @Test
    fun `present - selected users appear as such in search results`() = runTest {
        val repository = FakeUserRepository()
        val presenter = createDefaultInvitePeoplePresenter(
            userRepository = repository,
            coroutineDispatchers = testCoroutineDispatchers(useUnconfinedTestDispatcher = true)
        )
        presenter.test {
            val initialState = awaitItemAsDefault()
            skipItems(1)

            val selectedUser = aPRISMUser()

            initialState.eventSink(DefaultInvitePeopleEvents.ToggleUser(selectedUser))

            initialState.searchQuery.setTextAndPlaceCursorAtEnd("some query")
            skipItems(1)

            assertThat(repository.providedQuery).isEqualTo("some query")
            repository.emitStateWithUsers(users = aPRISMUserList() + selectedUser)
            skipItems(2)

            val resultState = awaitItemAsDefault()
            assertThat(resultState.searchResults).isInstanceOf(SearchBarResultState.Results::class.java)

            val users = resultState.searchResults.users()

            // The one user we have previously toggled is marked as selected
            val shouldBeSelectedUser = users.find { it.prismUser == selectedUser }
            assertThat(shouldBeSelectedUser).isNotNull()
            assertThat(shouldBeSelectedUser?.isSelected).isTrue()

            // And no others are
            val allOtherUsers = users.minus(shouldBeSelectedUser!!)
            assertThat(allOtherUsers.none { it.isSelected }).isTrue()
        }
    }

    @Test
    fun `present - toggling a user updates existing search results`() = runTest {
        val repository = FakeUserRepository()
        val presenter = createDefaultInvitePeoplePresenter(
            userRepository = repository,
            coroutineDispatchers = testCoroutineDispatchers(useUnconfinedTestDispatcher = true)
        )
        presenter.test {
            val initialState = awaitItemAsDefault()
            skipItems(1)

            val selectedUser = aPRISMUser()

            // Given a query is made
            initialState.searchQuery.setTextAndPlaceCursorAtEnd("some query")
            skipItems(1)

            assertThat(repository.providedQuery).isEqualTo("some query")
            repository.emitStateWithUsers(users = aPRISMUserList() + selectedUser)
            skipItems(1)
            awaitItemAsDefault().also { state ->
                // selectedUser is not selected
                assertThat(state.searchResults).isInstanceOf(SearchBarResultState.Results::class.java)
                val users = state.searchResults.users()
                val shouldNotBeSelectedUser = users.find { it.prismUser == selectedUser }
                assertThat(shouldNotBeSelectedUser).isNotNull()
                assertThat(shouldNotBeSelectedUser?.isSelected).isFalse()
            }

            // And then a user is toggled
            initialState.eventSink(DefaultInvitePeopleEvents.ToggleUser(selectedUser))
            skipItems(1)
            val resultState = awaitItemAsDefault()

            // The results are updated...
            assertThat(resultState.searchResults).isInstanceOf(SearchBarResultState.Results::class.java)
            val users = resultState.searchResults.users()

            // The one user we have now toggled is marked as selected
            val shouldBeSelectedUser = users.find { it.prismUser == selectedUser }
            assertThat(shouldBeSelectedUser).isNotNull()
            assertThat(shouldBeSelectedUser?.isSelected).isTrue()

            // And no others are
            val allOtherUsers = users.minus(shouldBeSelectedUser!!)
            assertThat(allOtherUsers.none { it.isSelected }).isTrue()
        }
    }

    @Test
    fun `present - toggling a user and send invite success`() = runTest {
        val repository = FakeUserRepository()
        val inviteUserResult = lambdaRecorder<UserId, Result<Unit>> { userId: UserId ->
            Result.success(Unit)
        }
        val presenter = createDefaultInvitePeoplePresenter(
            userRepository = repository,
            inviteUserResult = inviteUserResult,
            coroutineDispatchers = testCoroutineDispatchers(useUnconfinedTestDispatcher = true)
        )
        presenter.test {
            val initialState = awaitItem()
            skipItems(1)
            val selectedUser = aPRISMUser()
            repository.emitStateWithUsers(users = aPRISMUserList() + selectedUser)
            skipItems(1)
            // And then a user is toggled
            initialState.eventSink(DefaultInvitePeopleEvents.ToggleUser(selectedUser))
            skipItems(1)
            val resultState = awaitItemAsDefault()
            // The results are updated...
            assertThat(resultState.searchResults).isInstanceOf(SearchBarResultState.Results::class.java)
            // Send invites
            initialState.eventSink(InvitePeopleEvents.SendInvites)

            // Can't invite in the loading state
            awaitItem().run {
                assertThat(sendInvitesAction.isLoading()).isTrue()
                assertThat(canInvite).isFalse()
            }

            delay(1_000)
            inviteUserResult.assertions().isCalledOnce().with(
                value(selectedUser.userId)
            )

            // Can invite again once the action is finished
            awaitItem().run {
                assertThat(sendInvitesAction.isReady()).isTrue()
                assertThat(canInvite).isTrue()
            }
        }
    }

    @Test
    fun `present - toggling a user and send invite error`() = runTest {
        val repository = FakeUserRepository()
        val inviteUserResult = lambdaRecorder<UserId, Result<Unit>> { _: UserId ->
            Result.failure(AN_EXCEPTION)
        }
        val showErrorResResult = lambdaRecorder<Int, Int, Unit> { _, _ -> }
        val presenter = createDefaultInvitePeoplePresenter(
            userRepository = repository,
            inviteUserResult = inviteUserResult,
            coroutineDispatchers = testCoroutineDispatchers(useUnconfinedTestDispatcher = true),
            appErrorStateService = FakeAppErrorStateService(
                showErrorResResult = showErrorResResult,
            )
        )
        presenter.test {
            val initialState = awaitItem()
            skipItems(1)
            val selectedUser = aPRISMUser()
            repository.emitStateWithUsers(users = aPRISMUserList() + selectedUser)
            skipItems(1)
            // And then a user is toggled
            initialState.eventSink(DefaultInvitePeopleEvents.ToggleUser(selectedUser))
            skipItems(1)
            val resultState = awaitItemAsDefault()
            // The results are updated...
            assertThat(resultState.searchResults).isInstanceOf(SearchBarResultState.Results::class.java)
            // Send invites
            initialState.eventSink(InvitePeopleEvents.SendInvites)

            // Can't invite in the loading state
            awaitItem().run {
                assertThat(sendInvitesAction.isLoading()).isTrue()
                assertThat(canInvite).isFalse()
            }

            delay(1_000)
            inviteUserResult.assertions().isCalledOnce().with(
                value(selectedUser.userId)
            )
            showErrorResResult.assertions()
                .isCalledOnce()
                .with(
                    value(CommonStrings.common_unable_to_invite_title),
                    value(CommonStrings.common_unable_to_invite_message)
                )

            // Can invite again once the action is finished
            awaitItem().run {
                assertThat(sendInvitesAction.isReady()).isTrue()
                assertThat(canInvite).isTrue()
            }
        }
    }

    @Test
    fun `present - when joinedRoom is not provided, it is retrieved on the PRISMClient`() = runTest {
        val prismClient = FakePRISMClient().apply {
            givenGetRoomResult(A_ROOM_ID, FakeJoinedRoom())
        }
        val presenter = createDefaultInvitePeoplePresenter(
            joinedRoom = null,
            roomId = A_ROOM_ID,
            prismClient = prismClient,
        )
        presenter.test {
            val initialState = awaitItemAsDefault()
            assertThat(initialState.room.isLoading()).isTrue()
            val finalState = awaitItemAsDefault()
            assertThat(finalState.room).isEqualTo(AsyncData.Success(Unit))
        }
    }

    @Test
    fun `present - when joinedRoom is not provided, it is retrieved on the PRISMClient - error case`() = runTest {
        val prismClient = FakePRISMClient()
        val presenter = createDefaultInvitePeoplePresenter(
            joinedRoom = null,
            roomId = A_ROOM_ID,
            prismClient = prismClient,
        )
        presenter.test {
            val initialState = awaitItemAsDefault()
            assertThat(initialState.room.isLoading()).isTrue()
            val finalState = awaitItemAsDefault()
            assertThat(finalState.room.errorOrNull()?.message).isEqualTo("Room not found")
        }
    }

    @Test
    fun `present - suggestions are loaded from recent direct rooms`() = runTest {
        val dmRoomId = RoomId("!dm_room:server.org")
        val otherUserId = UserId("@frank:server.org")
        val prismClient = FakePRISMClient(sessionId = A_USER_ID).apply {
            // Track the DM room as recently visited
            trackRecentlyVisitedRoom(dmRoomId)
            // Set up a DM room with the other user
            givenGetRoomResult(
                dmRoomId,
                FakeBaseRoom(
                    sessionId = A_USER_ID,
                    roomId = dmRoomId,
                    initialRoomInfo = aRoomInfo(
                        id = dmRoomId,
                        isDirect = true,
                        activeMembersCount = 2,
                        currentUserMembership = CurrentUserMembership.JOINED,
                    ),
                    getDirectRoomMemberResult = { aRoomMember(userId = otherUserId, displayName = "Frank") }
                )
            )
        }
        val presenter = createDefaultInvitePeoplePresenter(
            prismClient = prismClient,
            // Use empty room members so the suggestion doesn't get filtered
            roomMembersState = RoomMembersState.Ready(persistentListOf()),
            coroutineDispatchers = testCoroutineDispatchers(useUnconfinedTestDispatcher = true),
        )
        presenter.test {
            skipItems(2)
            val state = awaitItemAsDefault()
            assertThat(state.suggestions).hasSize(1)
            assertThat(state.suggestions.first().prismUser.userId).isEqualTo(otherUserId)
            assertThat(state.suggestions.first().isSelected).isFalse()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `present - suggestions filters out existing room members`() = runTest {
        val dmRoomId = RoomId("!dm_room:server.org")
        val alreadyJoinedUserId = UserId("@frank:server.org")
        val prismClient = FakePRISMClient(sessionId = A_USER_ID).apply {
            trackRecentlyVisitedRoom(dmRoomId)
            givenGetRoomResult(
                dmRoomId,
                FakeBaseRoom(
                    sessionId = A_USER_ID,
                    roomId = dmRoomId,
                    initialRoomInfo = aRoomInfo(
                        id = dmRoomId,
                        isDirect = true,
                        activeMembersCount = 2,
                        currentUserMembership = CurrentUserMembership.JOINED,
                    ),
                    getDirectRoomMemberResult = { aRoomMember(userId = alreadyJoinedUserId, displayName = "Frank") }
                )
            )
        }
        // The user in the suggestion is already a member of the target room
        val presenter = createDefaultInvitePeoplePresenter(
            prismClient = prismClient,
            roomMembersState = RoomMembersState.Ready(
                persistentListOf(
                    aRoomMember(userId = alreadyJoinedUserId, membership = RoomMembershipState.JOIN)
                )
            ),
            coroutineDispatchers = testCoroutineDispatchers(useUnconfinedTestDispatcher = true),
        )
        presenter.test {
            skipItems(1)
            // The suggestion should be filtered out because the user is already a room member
            val state = awaitItemAsDefault()
            assertThat(state.suggestions).isEmpty()
            cancelAndIgnoreRemainingEvents()
        }
    }

    private suspend fun FakeUserRepository.emitStateWithUsers(
        users: List<PRISMUser>,
        isSearching: Boolean = false
    ) {
        emitState(
            results = users.map { UserSearchResult(it) },
            isSearching = isSearching,
        )
    }

    private suspend fun FakeUserRepository.emitState(
        results: List<UserSearchResult>,
        isSearching: Boolean = false
    ) {
        val state = UserSearchResultState(
            results = results,
            isSearching = isSearching
        )
        emitState(state)
    }

    private fun SearchBarResultState<ImmutableList<InvitableUser>>.users() =
        (this as? SearchBarResultState.Results<ImmutableList<InvitableUser>>)?.results.orEmpty()
}

private suspend fun <T> ReceiveTurbine<T>.awaitItemAsDefault(): DefaultInvitePeopleState {
    return awaitItem() as DefaultInvitePeopleState
}

fun TestScope.createDefaultInvitePeoplePresenter(
    roomMembersState: RoomMembersState = RoomMembersState.Ready(aRoomMemberList()),
    inviteUserResult: (UserId) -> Result<Unit> = { lambdaError() },
    joinedRoom: JoinedRoom? = FakeJoinedRoom(
        inviteUserResult = inviteUserResult,
    ).apply {
        givenRoomMembersState(roomMembersState)
    },
    roomId: RoomId = A_ROOM_ID,
    userRepository: UserRepository = FakeUserRepository(),
    coroutineDispatchers: CoroutineDispatchers = testCoroutineDispatchers(),
    appErrorStateService: AppErrorStateService = FakeAppErrorStateService(),
    prismClient: PRISMClient = FakePRISMClient(),
): DefaultInvitePeoplePresenter {
    return DefaultInvitePeoplePresenter(
        joinedRoom = joinedRoom,
        roomId = roomId,
        userRepository = userRepository,
        coroutineDispatchers = coroutineDispatchers,
        sessionCoroutineScope = backgroundScope,
        appErrorStateService = appErrorStateService,
        prismClient = prismClient,
    )
}
