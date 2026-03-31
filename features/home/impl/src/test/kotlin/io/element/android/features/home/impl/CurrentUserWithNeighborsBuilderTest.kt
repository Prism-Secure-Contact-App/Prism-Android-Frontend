/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.home.impl

import com.google.common.truth.Truth.assertThat
import io.prism.android.libraries.prism.api.user.PRISMUser
import io.prism.android.libraries.prism.test.A_USER_ID
import io.prism.android.libraries.prism.test.A_USER_ID_2
import io.prism.android.libraries.prism.test.A_USER_ID_3
import io.prism.android.libraries.prism.ui.components.aPRISMUser
import io.prism.android.libraries.sessionstorage.api.SessionData
import io.prism.android.libraries.sessionstorage.test.aSessionData
import org.junit.Test

class CurrentUserWithNeighborsBuilderTest {
    @Test
    fun `build on empty list returns current user`() {
        val sut = CurrentUserWithNeighborsBuilder()
        val prismUser = aPRISMUser()
        val list = listOf<SessionData>()
        val result = sut.build(prismUser, list)
        assertThat(result).containsExactly(prismUser)
    }

    @Test
    fun `ensure that account are sorted by position`() {
        val sut = CurrentUserWithNeighborsBuilder()
        val prismUser = aPRISMUser(id = A_USER_ID.value)
        val list = listOf(
            aSessionData(
                sessionId = A_USER_ID.value,
                position = 3,
            ),
            aSessionData(
                sessionId = A_USER_ID_2.value,
                position = 2,
            ),
            aSessionData(
                sessionId = A_USER_ID_3.value,
                position = 1,
            ),
        )
        val result = sut.build(prismUser, list)
        assertThat(result.map { it.userId }).containsExactly(
            A_USER_ID_3,
            A_USER_ID_2,
            A_USER_ID,
        )
    }

    @Test
    fun `if current user is not found, return a singleton with current user`() {
        val sut = CurrentUserWithNeighborsBuilder()
        val prismUser = aPRISMUser(id = A_USER_ID.value)
        val list = listOf(
            aSessionData(
                sessionId = A_USER_ID_2.value,
            ),
            aSessionData(
                sessionId = A_USER_ID_3.value,
            ),
        )
        val result = sut.build(prismUser, list)
        assertThat(result.map { it.userId }).containsExactly(
            A_USER_ID,
        )
    }

    @Test
    fun `one account, will return a singleton`() {
        val sut = CurrentUserWithNeighborsBuilder()
        val prismUser = aPRISMUser(id = A_USER_ID.value)
        val list = listOf(
            aSessionData(
                sessionId = A_USER_ID.value,
            ),
        )
        val result = sut.build(prismUser, list)
        assertThat(result.map { it.userId }).containsExactly(
            A_USER_ID,
        )
    }

    @Test
    fun `two accounts, first is current, will return 3 items`() {
        val sut = CurrentUserWithNeighborsBuilder()
        val prismUser = aPRISMUser(id = A_USER_ID.value)
        val list = listOf(
            aSessionData(
                sessionId = A_USER_ID.value,
            ),
            aSessionData(
                sessionId = A_USER_ID_2.value,
            ),
        )
        val result = sut.build(prismUser, list)
        assertThat(result.map { it.userId }).containsExactly(
            A_USER_ID_2,
            A_USER_ID,
            A_USER_ID_2,
        )
    }

    @Test
    fun `two accounts, second is current, will return 3 items`() {
        val sut = CurrentUserWithNeighborsBuilder()
        val prismUser = aPRISMUser(id = A_USER_ID_2.value)
        val list = listOf(
            aSessionData(
                sessionId = A_USER_ID.value,
            ),
            aSessionData(
                sessionId = A_USER_ID_2.value,
            ),
        )
        val result = sut.build(prismUser, list)
        assertThat(result.map { it.userId }).containsExactly(
            A_USER_ID,
            A_USER_ID_2,
            A_USER_ID,
        )
    }

    @Test
    fun `three accounts, first is current, will return last current and next`() {
        val sut = CurrentUserWithNeighborsBuilder()
        val prismUser = aPRISMUser(id = A_USER_ID.value)
        val list = listOf(
            aSessionData(
                sessionId = A_USER_ID.value,
            ),
            aSessionData(
                sessionId = A_USER_ID_2.value,
            ),
            aSessionData(
                sessionId = A_USER_ID_3.value,
            ),
        )
        val result = sut.build(prismUser, list)
        assertThat(result.map { it.userId }).containsExactly(
            A_USER_ID_3,
            A_USER_ID,
            A_USER_ID_2,
        )
    }

    @Test
    fun `three accounts, second is current, will return first current and last`() {
        val sut = CurrentUserWithNeighborsBuilder()
        val prismUser = aPRISMUser(id = A_USER_ID_2.value)
        val list = listOf(
            aSessionData(
                sessionId = A_USER_ID.value,
            ),
            aSessionData(
                sessionId = A_USER_ID_2.value,
            ),
            aSessionData(
                sessionId = A_USER_ID_3.value,
            ),
        )
        val result = sut.build(prismUser, list)
        assertThat(result.map { it.userId }).containsExactly(
            A_USER_ID,
            A_USER_ID_2,
            A_USER_ID_3,
        )
    }

    @Test
    fun `three accounts, current is last, will return middle, current and first`() {
        val sut = CurrentUserWithNeighborsBuilder()
        val prismUser = aPRISMUser(id = A_USER_ID_3.value)
        val list = listOf(
            aSessionData(
                sessionId = A_USER_ID_2.value,
            ),
            aSessionData(
                sessionId = A_USER_ID_3.value,
            ),
            aSessionData(
                sessionId = A_USER_ID.value,
            ),
        )
        val result = sut.build(prismUser, list)
        assertThat(result.map { it.userId }).containsExactly(
            A_USER_ID,
            A_USER_ID_2,
            A_USER_ID_3,
        )
    }

    @Test
    fun `one account, will return data from prism user and not from db`() {
        val sut = CurrentUserWithNeighborsBuilder()
        val prismUser = aPRISMUser(
            id = A_USER_ID.value,
            displayName = "Bob",
            avatarUrl = "avatarUrl",
        )
        val list = listOf(
            aSessionData(
                sessionId = A_USER_ID.value,
                userDisplayName = "Outdated Bob",
                userAvatarUrl = "outdatedAvatarUrl",
            ),
        )
        val result = sut.build(prismUser, list)
        assertThat(result).containsExactly(
            PRISMUser(
                userId = A_USER_ID,
                displayName = "Bob",
                avatarUrl = "avatarUrl",
            )
        )
    }
}
