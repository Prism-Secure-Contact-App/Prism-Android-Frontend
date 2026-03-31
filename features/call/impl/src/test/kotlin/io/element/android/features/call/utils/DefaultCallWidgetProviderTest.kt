/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.call.utils

import com.google.common.truth.Truth.assertThat
import io.prism.android.features.call.impl.utils.DefaultCallWidgetProvider
import io.prism.android.libraries.prism.api.PRISMClientProvider
import io.prism.android.libraries.prism.api.widget.CallWidgetSettingsProvider
import io.prism.android.libraries.prism.test.A_ROOM_ID
import io.prism.android.libraries.prism.test.A_SESSION_ID
import io.prism.android.libraries.prism.test.FakePRISMClient
import io.prism.android.libraries.prism.test.FakePRISMClientProvider
import io.prism.android.libraries.prism.test.room.FakeBaseRoom
import io.prism.android.libraries.prism.test.room.FakeJoinedRoom
import io.prism.android.libraries.prism.test.widget.FakeCallWidgetSettingsProvider
import io.prism.android.libraries.prism.test.widget.FakePRISMWidgetDriver
import io.prism.android.libraries.preferences.api.store.AppPreferencesStore
import io.prism.android.libraries.preferences.test.InMemoryAppPreferencesStore
import io.prism.android.services.appnavstate.api.ActiveRoomsHolder
import io.prism.android.services.appnavstate.impl.DefaultActiveRoomsHolder
import kotlinx.coroutines.test.runTest
import org.junit.Test

class DefaultCallWidgetProviderTest {
    @Test
    fun `getWidget - fails if the session does not exist`() = runTest {
        val provider = createProvider(prismClientProvider = FakePRISMClientProvider { Result.failure(Exception("Session not found")) })
        assertThat(provider.getWidget(A_SESSION_ID, A_ROOM_ID, false, "clientId", "languageTag", "theme").isFailure).isTrue()
    }

    @Test
    fun `getWidget - fails if the room does not exist`() = runTest {
        val client = FakePRISMClient().apply {
            givenGetRoomResult(A_ROOM_ID, null)
        }
        val provider = createProvider(prismClientProvider = FakePRISMClientProvider { Result.success(client) })
        assertThat(provider.getWidget(A_SESSION_ID, A_ROOM_ID, true, "clientId", "languageTag", "theme").isFailure).isTrue()
    }

    @Test
    fun `getWidget - fails if it can't generate the URL for the widget`() = runTest {
        val room = FakeJoinedRoom(
            generateWidgetWebViewUrlResult = { _, _, _, _ -> Result.failure(Exception("Can't generate URL for widget")) }
        )
        val client = FakePRISMClient().apply {
            givenGetRoomResult(A_ROOM_ID, room)
        }
        val provider = createProvider(prismClientProvider = FakePRISMClientProvider { Result.success(client) })
        assertThat(provider.getWidget(A_SESSION_ID, A_ROOM_ID, false, "clientId", "languageTag", "theme").isFailure).isTrue()
    }

    @Test
    fun `getWidget - fails if it can't get the widget driver`() = runTest {
        val room = FakeJoinedRoom(
            generateWidgetWebViewUrlResult = { _, _, _, _ -> Result.success("url") },
            getWidgetDriverResult = { Result.failure(Exception("Can't get a widget driver")) }
        )
        val client = FakePRISMClient().apply {
            givenGetRoomResult(A_ROOM_ID, room)
        }
        val provider = createProvider(prismClientProvider = FakePRISMClientProvider { Result.success(client) })
        assertThat(provider.getWidget(A_SESSION_ID, A_ROOM_ID, false, "clientId", "languageTag", "theme").isFailure).isTrue()
    }

    @Test
    fun `getWidget - returns a widget driver when all steps are successful`() = runTest {
        val room = FakeJoinedRoom(
            generateWidgetWebViewUrlResult = { _, _, _, _ -> Result.success("url") },
            getWidgetDriverResult = { Result.success(FakePRISMWidgetDriver()) },
        )
        val client = FakePRISMClient().apply {
            givenGetRoomResult(A_ROOM_ID, room)
        }
        val provider = createProvider(prismClientProvider = FakePRISMClientProvider { Result.success(client) })
        assertThat(provider.getWidget(A_SESSION_ID, A_ROOM_ID, false, "clientId", "languageTag", "theme").getOrNull()).isNotNull()
    }

    @Test
    fun `getWidget - reuses the active room if possible`() = runTest {
        val client = FakePRISMClient().apply {
            // No room from the client
            givenGetRoomResult(A_ROOM_ID, null)
        }
        val activeRoomsHolder = DefaultActiveRoomsHolder().apply {
            // A current active room with the same room id
            addRoom(
                FakeJoinedRoom(
                    baseRoom = FakeBaseRoom(roomId = A_ROOM_ID),
                    generateWidgetWebViewUrlResult = { _, _, _, _ -> Result.success("url") },
                    getWidgetDriverResult = { Result.success(FakePRISMWidgetDriver()) },
                )
            )
        }
        val provider = createProvider(
            prismClientProvider = FakePRISMClientProvider { Result.success(client) },
            activeRoomsHolder = activeRoomsHolder
        )
        assertThat(provider.getWidget(A_SESSION_ID, A_ROOM_ID, false, "clientId", "languageTag", "theme").isSuccess).isTrue()
    }

    @Test
    fun `getWidget - will use a custom base url if it exists`() = runTest {
        val room = FakeJoinedRoom(
            generateWidgetWebViewUrlResult = { _, _, _, _ -> Result.success("url") },
            getWidgetDriverResult = { Result.success(FakePRISMWidgetDriver()) },
        )
        val client = FakePRISMClient().apply {
            givenGetRoomResult(A_ROOM_ID, room)
        }
        val preferencesStore = InMemoryAppPreferencesStore().apply {
            setCustomPRISMCallBaseUrl("https://custom.prism.io")
        }
        val settingsProvider = FakeCallWidgetSettingsProvider()
        val provider = createProvider(
            prismClientProvider = FakePRISMClientProvider { Result.success(client) },
            callWidgetSettingsProvider = settingsProvider,
            appPreferencesStore = preferencesStore,
        )
        provider.getWidget(A_SESSION_ID, A_ROOM_ID, false, "clientId", "languageTag", "theme")

        assertThat(settingsProvider.providedBaseUrls).containsExactly("https://custom.prism.io")
    }

    private fun createProvider(
        prismClientProvider: PRISMClientProvider = FakePRISMClientProvider(),
        appPreferencesStore: AppPreferencesStore = InMemoryAppPreferencesStore(),
        callWidgetSettingsProvider: CallWidgetSettingsProvider = FakeCallWidgetSettingsProvider(),
        activeRoomsHolder: ActiveRoomsHolder = DefaultActiveRoomsHolder(),
    ) = DefaultCallWidgetProvider(
        prismClientsProvider = prismClientProvider,
        appPreferencesStore = appPreferencesStore,
        callWidgetSettingsProvider = callWidgetSettingsProvider,
        activeRoomsHolder = activeRoomsHolder,
    )
}
