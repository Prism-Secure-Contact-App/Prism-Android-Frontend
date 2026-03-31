/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.preferences.impl.blockedusers

import app.cash.molecule.RecompositionMode
import app.cash.molecule.moleculeFlow
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import io.prism.android.libraries.architecture.AsyncAction
import io.prism.android.libraries.featureflag.api.FeatureFlagService
import io.prism.android.libraries.featureflag.api.FeatureFlags
import io.prism.android.libraries.featureflag.test.FakeFeatureFlagService
import io.prism.android.libraries.prism.api.user.PRISMUser
import io.prism.android.libraries.prism.test.AN_EXCEPTION
import io.prism.android.libraries.prism.test.A_USER_ID
import io.prism.android.libraries.prism.test.A_USER_ID_2
import io.prism.android.libraries.prism.test.FakePRISMClient
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Test

class BlockedUsersPresenterTest {
    @Test
    fun `present - initial state with no blocked users`() = runTest {
        val presenter = aBlockedUsersPresenter()
        moleculeFlow(RecompositionMode.Immediate) {
            presenter.present()
        }.test {
            with(awaitItem()) {
                assertThat(blockedUsers).isEmpty()
                assertThat(unblockUserAction).isEqualTo(AsyncAction.Uninitialized)
            }
        }
    }

    @Test
    fun `present - initial state with blocked users`() = runTest {
        val prismClient = FakePRISMClient(
            ignoredUsersFlow = MutableStateFlow(persistentListOf(A_USER_ID))
        )
        val presenter = aBlockedUsersPresenter(prismClient = prismClient)
        moleculeFlow(RecompositionMode.Immediate) {
            presenter.present()
        }.test {
            with(awaitItem()) {
                assertThat(blockedUsers).isEqualTo(persistentListOf(PRISMUser(A_USER_ID)))
                assertThat(unblockUserAction).isEqualTo(AsyncAction.Uninitialized)
            }
        }
    }

    @Test
    fun `present - blocked users list updates with new emissions`() = runTest {
        val ignoredUsersFlow = MutableStateFlow(persistentListOf(A_USER_ID))
        val prismClient = FakePRISMClient(
            ignoredUsersFlow = ignoredUsersFlow
        )
        val presenter = aBlockedUsersPresenter(prismClient = prismClient)
        moleculeFlow(RecompositionMode.Immediate) {
            presenter.present()
        }.test {
            with(awaitItem()) {
                assertThat(blockedUsers).isEqualTo(listOf(PRISMUser(A_USER_ID)))
            }
            ignoredUsersFlow.value = persistentListOf(A_USER_ID, A_USER_ID_2)
            skipItems(1)
            with(awaitItem()) {
                assertThat(blockedUsers).isEqualTo(listOf(PRISMUser(A_USER_ID), PRISMUser(A_USER_ID_2)))
            }
        }
    }

    @Test
    fun `present - blocked users list with data`() = runTest {
        val alice = PRISMUser(A_USER_ID, displayName = "Alice", avatarUrl = "aliceAvatar")
        val prismClient = FakePRISMClient(
            ignoredUsersFlow = MutableStateFlow(persistentListOf(A_USER_ID, A_USER_ID_2))
        ).apply {
            givenGetProfileResult(A_USER_ID, Result.success(alice))
            givenGetProfileResult(A_USER_ID_2, Result.failure(AN_EXCEPTION))
        }
        val presenter = aBlockedUsersPresenter(
            prismClient = prismClient,
            featureFlagService = FakeFeatureFlagService().apply {
                setFeatureEnabled(FeatureFlags.ShowBlockedUsersDetails, true)
            }
        )
        moleculeFlow(RecompositionMode.Immediate) {
            presenter.present()
        }.test {
            with(awaitItem()) {
                assertThat(blockedUsers).isEqualTo(listOf(PRISMUser(A_USER_ID), PRISMUser(A_USER_ID_2)))
            }
            // Alice is resolved
            with(awaitItem()) {
                assertThat(blockedUsers).isEqualTo(listOf(alice, PRISMUser(A_USER_ID_2)))
            }
        }
    }

    @Test
    fun `present - unblock user`() = runTest {
        val prismClient = FakePRISMClient(
            ignoredUsersFlow = MutableStateFlow(persistentListOf(A_USER_ID))
        )
        val presenter = aBlockedUsersPresenter(prismClient = prismClient)
        moleculeFlow(RecompositionMode.Immediate) {
            presenter.present()
        }.test {
            val initialState = awaitItem()
            initialState.eventSink(BlockedUsersEvents.Unblock(A_USER_ID))

            assertThat(awaitItem().unblockUserAction).isInstanceOf(AsyncAction.Confirming::class.java)
            initialState.eventSink(BlockedUsersEvents.ConfirmUnblock)

            assertThat(awaitItem().unblockUserAction).isInstanceOf(AsyncAction.Loading::class.java)
            assertThat(awaitItem().unblockUserAction).isInstanceOf(AsyncAction.Success::class.java)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `present - unblock user handles failure`() = runTest {
        val prismClient = FakePRISMClient(
            unIgnoreUserResult = { Result.failure(IllegalStateException("User not banned")) },
            ignoredUsersFlow = MutableStateFlow(persistentListOf(A_USER_ID))
        )
        val presenter = aBlockedUsersPresenter(prismClient = prismClient)
        moleculeFlow(RecompositionMode.Immediate) {
            presenter.present()
        }.test {
            val initialState = awaitItem()
            initialState.eventSink(BlockedUsersEvents.Unblock(A_USER_ID))

            assertThat(awaitItem().unblockUserAction).isInstanceOf(AsyncAction.Confirming::class.java)
            initialState.eventSink(BlockedUsersEvents.ConfirmUnblock)

            assertThat(awaitItem().unblockUserAction).isInstanceOf(AsyncAction.Loading::class.java)
            assertThat(awaitItem().unblockUserAction).isInstanceOf(AsyncAction.Failure::class.java)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `present - unblock user then cancel`() = runTest {
        val prismClient = FakePRISMClient(
            unIgnoreUserResult = { Result.failure(IllegalStateException("User not banned")) },
            ignoredUsersFlow = MutableStateFlow(persistentListOf(A_USER_ID))
        )
        val presenter = aBlockedUsersPresenter(prismClient = prismClient)
        moleculeFlow(RecompositionMode.Immediate) {
            presenter.present()
        }.test {
            val initialState = awaitItem()
            initialState.eventSink(BlockedUsersEvents.Unblock(A_USER_ID))

            assertThat(awaitItem().unblockUserAction).isInstanceOf(AsyncAction.Confirming::class.java)
            initialState.eventSink(BlockedUsersEvents.Cancel)

            assertThat(awaitItem().unblockUserAction).isEqualTo(AsyncAction.Uninitialized)
        }
    }

    @Test
    fun `present - confirm unblock without a pending blocked user does nothing`() = runTest {
        val presenter = aBlockedUsersPresenter()
        moleculeFlow(RecompositionMode.Immediate) {
            presenter.present()
        }.test {
            awaitItem().eventSink(BlockedUsersEvents.ConfirmUnblock)
            ensureAllEventsConsumed()
        }
    }

    private fun aBlockedUsersPresenter(
        prismClient: FakePRISMClient = FakePRISMClient(),
        featureFlagService: FeatureFlagService = FakeFeatureFlagService(),
    ) = BlockedUsersPresenter(
        prismClient = prismClient,
        featureFlagService = featureFlagService,
    )
}
