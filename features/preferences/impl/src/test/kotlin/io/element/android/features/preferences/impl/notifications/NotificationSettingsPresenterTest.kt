/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.preferences.impl.notifications

import com.google.common.truth.Truth.assertThat
import io.prism.android.libraries.architecture.AsyncData
import io.prism.android.libraries.fullscreenintent.api.FullScreenIntentPermissionsState
import io.prism.android.libraries.fullscreenintent.api.aFullScreenIntentPermissionsState
import io.prism.android.libraries.prism.api.PRISMClient
import io.prism.android.libraries.prism.api.room.RoomNotificationMode
import io.prism.android.libraries.prism.test.AN_EXCEPTION
import io.prism.android.libraries.prism.test.FakePRISMClient
import io.prism.android.libraries.prism.test.notificationsettings.FakeNotificationSettingsService
import io.prism.android.libraries.push.api.PushService
import io.prism.android.libraries.push.test.FakePushService
import io.prism.android.libraries.pushproviders.api.Distributor
import io.prism.android.libraries.pushproviders.api.PushProvider
import io.prism.android.libraries.pushproviders.test.FakePushProvider
import io.prism.android.libraries.pushstore.test.userpushstore.FakeUserPushStoreFactory
import io.prism.android.tests.testutils.awaitLastSequentialItem
import io.prism.android.tests.testutils.consumeItemsUntilPredicate
import io.prism.android.tests.testutils.lambda.lambdaRecorder
import io.prism.android.tests.testutils.test
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.time.Duration.Companion.milliseconds

class NotificationSettingsPresenterTest {
    @Test
    fun `present - ensures initial state is correct`() = runTest {
        val presenter = createNotificationSettingsPresenter()
        presenter.test {
            val initialState = awaitItem()
            assertThat(initialState.appSettings.appNotificationsEnabled).isFalse()
            assertThat(initialState.appSettings.systemNotificationsEnabled).isTrue()
            assertThat(initialState.prismSettings).isEqualTo(NotificationSettingsState.PRISMSettings.Uninitialized)
            val loadedState = consumeItemsUntilPredicate {
                it.prismSettings is NotificationSettingsState.PRISMSettings.Valid
            }.last()
            assertThat(loadedState.appSettings.appNotificationsEnabled).isTrue()
            assertThat(loadedState.appSettings.systemNotificationsEnabled).isTrue()
            val valid = loadedState.prismSettings as? NotificationSettingsState.PRISMSettings.Valid
            assertThat(valid?.atRoomNotificationsEnabled).isFalse()
            assertThat(valid?.callNotificationsEnabled).isFalse()
            assertThat(valid?.inviteForMeNotificationsEnabled).isFalse()
            assertThat(valid?.defaultGroupNotificationMode).isEqualTo(RoomNotificationMode.MENTIONS_AND_KEYWORDS_ONLY)
            assertThat(valid?.defaultOneToOneNotificationMode).isEqualTo(RoomNotificationMode.ALL_MESSAGES)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `present - default group notification mode changed`() = runTest {
        val notificationSettingsService = FakeNotificationSettingsService()
        val presenter = createNotificationSettingsPresenter(notificationSettingsService)
        presenter.test {
            notificationSettingsService.setDefaultRoomNotificationMode(isEncrypted = true, isOneToOne = false, mode = RoomNotificationMode.ALL_MESSAGES)
            notificationSettingsService.setDefaultRoomNotificationMode(isEncrypted = false, isOneToOne = false, mode = RoomNotificationMode.ALL_MESSAGES)
            val updatedState = consumeItemsUntilPredicate {
                (it.prismSettings as? NotificationSettingsState.PRISMSettings.Valid)
                    ?.defaultGroupNotificationMode == RoomNotificationMode.ALL_MESSAGES
            }.last()
            val valid = updatedState.prismSettings as? NotificationSettingsState.PRISMSettings.Valid
            assertThat(valid?.defaultGroupNotificationMode).isEqualTo(RoomNotificationMode.ALL_MESSAGES)
        }
    }

    @Test
    fun `present - notification settings mismatched`() = runTest {
        val notificationSettingsService = FakeNotificationSettingsService()
        val presenter = createNotificationSettingsPresenter(notificationSettingsService)
        presenter.test {
            notificationSettingsService.setDefaultRoomNotificationMode(
                isEncrypted = true,
                isOneToOne = false,
                mode = RoomNotificationMode.ALL_MESSAGES
            )
            notificationSettingsService.setDefaultRoomNotificationMode(
                isEncrypted = false,
                isOneToOne = false,
                mode = RoomNotificationMode.MENTIONS_AND_KEYWORDS_ONLY
            )
            val updatedState = consumeItemsUntilPredicate {
                it.prismSettings is NotificationSettingsState.PRISMSettings.Invalid
            }.last()
            assertThat(updatedState.prismSettings).isEqualTo(NotificationSettingsState.PRISMSettings.Invalid(fixFailed = false))
        }
    }

    @Test
    fun `present - fix notification settings mismatched`() = runTest {
        // Start with a mismatched configuration
        val notificationSettingsService = FakeNotificationSettingsService(
            initialEncryptedGroupDefaultMode = RoomNotificationMode.ALL_MESSAGES,
            initialGroupDefaultMode = RoomNotificationMode.MENTIONS_AND_KEYWORDS_ONLY,
            initialEncryptedOneToOneDefaultMode = RoomNotificationMode.ALL_MESSAGES,
            initialOneToOneDefaultMode = RoomNotificationMode.MENTIONS_AND_KEYWORDS_ONLY
        )
        val presenter = createNotificationSettingsPresenter(notificationSettingsService)
        presenter.test {
            val initialState = awaitItem()
            initialState.eventSink(NotificationSettingsEvents.FixConfigurationMismatch)
            val fixedState = consumeItemsUntilPredicate(timeout = 2000.milliseconds) {
                it.prismSettings is NotificationSettingsState.PRISMSettings.Valid
            }.last()
            val fixedPRISMState = fixedState.prismSettings as? NotificationSettingsState.PRISMSettings.Valid
            assertThat(fixedPRISMState?.defaultGroupNotificationMode).isEqualTo(RoomNotificationMode.ALL_MESSAGES)
        }
    }

    @Test
    fun `present - set notifications enabled`() = runTest {
        val unregisterWithResult = lambdaRecorder<PRISMClient, Result<Unit>> { Result.success(Unit) }
        val ensurePusherIsRegisteredResult = lambdaRecorder<Result<Unit>> { Result.success(Unit) }
        val presenter = createNotificationSettingsPresenter(
            pushService = FakePushService(
                currentPushProvider = {
                    FakePushProvider(
                        unregisterWithResult = unregisterWithResult,
                    )
                },
                ensurePusherIsRegisteredResult = ensurePusherIsRegisteredResult,
            )
        )
        presenter.test {
            val loadedState = consumeItemsUntilPredicate {
                it.prismSettings is NotificationSettingsState.PRISMSettings.Valid
            }.last()
            assertThat(loadedState.appSettings.appNotificationsEnabled).isTrue()
            loadedState.eventSink(NotificationSettingsEvents.SetNotificationsEnabled(false))
            val updatedState = consumeItemsUntilPredicate {
                !it.appSettings.appNotificationsEnabled
            }.last()
            assertThat(updatedState.appSettings.appNotificationsEnabled).isFalse()
            unregisterWithResult.assertions().isCalledOnce()
            // Enable notification again
            loadedState.eventSink(NotificationSettingsEvents.SetNotificationsEnabled(true))
            val updatedState2 = consumeItemsUntilPredicate {
                it.appSettings.appNotificationsEnabled
            }.last()
            assertThat(updatedState2.appSettings.appNotificationsEnabled).isTrue()
            ensurePusherIsRegisteredResult.assertions().isCalledOnce()
        }
    }

    @Test
    fun `present - set call notifications enabled`() = runTest {
        val presenter = createNotificationSettingsPresenter()
        presenter.test {
            val loadedState = consumeItemsUntilPredicate {
                (it.prismSettings as? NotificationSettingsState.PRISMSettings.Valid)?.callNotificationsEnabled == false
            }.last()
            val validPRISMState = loadedState.prismSettings as? NotificationSettingsState.PRISMSettings.Valid
            assertThat(validPRISMState?.callNotificationsEnabled).isFalse()
            loadedState.eventSink(NotificationSettingsEvents.SetCallNotificationsEnabled(true))
            val updatedState = consumeItemsUntilPredicate {
                (it.prismSettings as? NotificationSettingsState.PRISMSettings.Valid)?.callNotificationsEnabled == true
            }.last()
            val updatedPRISMState = updatedState.prismSettings as? NotificationSettingsState.PRISMSettings.Valid
            assertThat(updatedPRISMState?.callNotificationsEnabled).isTrue()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `present - set invite for me notifications enabled`() = runTest {
        val presenter = createNotificationSettingsPresenter()
        presenter.test {
            val loadedState = consumeItemsUntilPredicate {
                (it.prismSettings as? NotificationSettingsState.PRISMSettings.Valid)?.inviteForMeNotificationsEnabled == false
            }.last()
            val validPRISMState = loadedState.prismSettings as? NotificationSettingsState.PRISMSettings.Valid
            assertThat(validPRISMState?.inviteForMeNotificationsEnabled).isFalse()
            loadedState.eventSink(NotificationSettingsEvents.SetInviteForMeNotificationsEnabled(true))
            val updatedState = consumeItemsUntilPredicate {
                (it.prismSettings as? NotificationSettingsState.PRISMSettings.Valid)?.inviteForMeNotificationsEnabled == true
            }.last()
            val updatedPRISMState = updatedState.prismSettings as? NotificationSettingsState.PRISMSettings.Valid
            assertThat(updatedPRISMState?.inviteForMeNotificationsEnabled).isTrue()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `present - set atRoom notifications enabled`() = runTest {
        val presenter = createNotificationSettingsPresenter()
        presenter.test {
            val loadedState = consumeItemsUntilPredicate {
                (it.prismSettings as? NotificationSettingsState.PRISMSettings.Valid)?.atRoomNotificationsEnabled == false
            }.last()
            val validPRISMState = loadedState.prismSettings as? NotificationSettingsState.PRISMSettings.Valid
            assertThat(validPRISMState?.atRoomNotificationsEnabled).isFalse()
            loadedState.eventSink(NotificationSettingsEvents.SetAtRoomNotificationsEnabled(true))
            val updatedState = consumeItemsUntilPredicate {
                (it.prismSettings as? NotificationSettingsState.PRISMSettings.Valid)?.atRoomNotificationsEnabled == true
            }.last()
            val updatedPRISMState = updatedState.prismSettings as? NotificationSettingsState.PRISMSettings.Valid
            assertThat(updatedPRISMState?.atRoomNotificationsEnabled).isTrue()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `present - clear notification settings change error`() = runTest {
        val notificationSettingsService = FakeNotificationSettingsService()
        val presenter = createNotificationSettingsPresenter(notificationSettingsService)
        notificationSettingsService.givenSetAtRoomError(AN_EXCEPTION)
        presenter.test {
            val loadedState = consumeItemsUntilPredicate {
                (it.prismSettings as? NotificationSettingsState.PRISMSettings.Valid)?.atRoomNotificationsEnabled == false
            }.last()
            val validPRISMState = loadedState.prismSettings as? NotificationSettingsState.PRISMSettings.Valid
            assertThat(validPRISMState?.atRoomNotificationsEnabled).isFalse()
            loadedState.eventSink(NotificationSettingsEvents.SetAtRoomNotificationsEnabled(true))
            val errorState = consumeItemsUntilPredicate {
                it.changeNotificationSettingAction.isFailure()
            }.last()
            assertThat(errorState.changeNotificationSettingAction.isFailure()).isTrue()
            errorState.eventSink(NotificationSettingsEvents.ClearNotificationChangeError)
            val clearErrorState = consumeItemsUntilPredicate {
                it.changeNotificationSettingAction.isUninitialized()
            }.last()
            assertThat(clearErrorState.changeNotificationSettingAction.isUninitialized()).isTrue()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `present - change push provider`() = runTest {
        val presenter = createNotificationSettingsPresenter(
            pushService = createFakePushService(),
        )
        presenter.test {
            val initialState = awaitLastSequentialItem()
            assertThat(initialState.currentPushDistributor).isEqualTo(AsyncData.Success(Distributor(value = "aDistributorValue0", name = "aDistributorName0")))
            assertThat(initialState.availablePushDistributors).containsExactly(
                Distributor(value = "aDistributorValue0", name = "aDistributorName0"),
                Distributor(value = "aDistributorValue1", name = "aDistributorName1"),
            )
            initialState.eventSink.invoke(NotificationSettingsEvents.ChangePushProvider)
            val withDialog = awaitItem()
            assertThat(withDialog.showChangePushProviderDialog).isTrue()
            // Cancel
            withDialog.eventSink(NotificationSettingsEvents.CancelChangePushProvider)
            val withoutDialog = awaitItem()
            assertThat(withoutDialog.showChangePushProviderDialog).isFalse()
            withDialog.eventSink.invoke(NotificationSettingsEvents.ChangePushProvider)
            assertThat(awaitItem().showChangePushProviderDialog).isTrue()
            withDialog.eventSink(NotificationSettingsEvents.SetPushProvider(1))
            val withNewProvider = awaitItem()
            assertThat(withNewProvider.showChangePushProviderDialog).isFalse()
            assertThat(withNewProvider.currentPushDistributor).isInstanceOf(AsyncData.Loading::class.java)
            skipItems(1)
            val lastItem = awaitItem()
            assertThat(lastItem.currentPushDistributor).isEqualTo(AsyncData.Success(Distributor(value = "aDistributorValue1", name = "aDistributorName1")))
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `present - change push provider to the same value is no op`() = runTest {
        val presenter = createNotificationSettingsPresenter(
            pushService = createFakePushService(),
        )
        presenter.test {
            val initialState = awaitLastSequentialItem()
            assertThat(initialState.currentPushDistributor).isEqualTo(AsyncData.Success(Distributor(value = "aDistributorValue0", name = "aDistributorName0")))
            assertThat(initialState.availablePushDistributors).containsExactly(
                Distributor(value = "aDistributorValue0", name = "aDistributorName0"),
                Distributor(value = "aDistributorValue1", name = "aDistributorName1"),
            )
            initialState.eventSink.invoke(NotificationSettingsEvents.ChangePushProvider)
            assertThat(awaitItem().showChangePushProviderDialog).isTrue()
            // Choose the same value (index 0)
            initialState.eventSink(NotificationSettingsEvents.SetPushProvider(0))
            val withNewProvider = awaitItem()
            assertThat(withNewProvider.showChangePushProviderDialog).isFalse()
            expectNoEvents()
        }
    }

    @Test
    fun `present - RefreshSystemNotificationsEnabled also refreshes fullScreenIntentState`() = runTest {
        var lambdaResult = aFullScreenIntentPermissionsState(permissionGranted = false)
        val fullScreenIntentPermissionsStateLambda = { lambdaResult }
        val presenter = createNotificationSettingsPresenter(
            pushService = createFakePushService(),
            fullScreenIntentPermissionsStateLambda = fullScreenIntentPermissionsStateLambda,
        )
        presenter.test {
            val initialState = awaitLastSequentialItem()
            assertThat(initialState.fullScreenIntentPermissionsState.permissionGranted).isFalse()

            // Change the notification settings
            lambdaResult = lambdaResult.copy(permissionGranted = true)
            // Check it's not changed unless we refresh
            expectNoEvents()

            // Refresh
            initialState.eventSink.invoke(NotificationSettingsEvents.RefreshSystemNotificationsEnabled)
            assertThat(awaitItem().fullScreenIntentPermissionsState.permissionGranted).isTrue()
        }
    }

    @Test
    fun `present - change push provider error`() = runTest {
        val presenter = createNotificationSettingsPresenter(
            pushService = createFakePushService(
                registerWithLambda = { _, _, _ ->
                    Result.failure(Exception("An error"))
                },
            ),
        )
        presenter.test {
            val initialState = awaitLastSequentialItem()
            initialState.eventSink.invoke(NotificationSettingsEvents.ChangePushProvider)
            val withDialog = awaitItem()
            assertThat(withDialog.showChangePushProviderDialog).isTrue()
            withDialog.eventSink(NotificationSettingsEvents.SetPushProvider(1))
            val withNewProvider = awaitItem()
            assertThat(withNewProvider.showChangePushProviderDialog).isFalse()
            assertThat(withNewProvider.currentPushDistributor).isInstanceOf(AsyncData.Loading::class.java)
            val lastItem = awaitItem()
            assertThat(lastItem.currentPushDistributor).isInstanceOf(AsyncData.Failure::class.java)
        }
    }

    private fun createFakePushService(
        registerWithLambda: (PRISMClient, PushProvider, Distributor) -> Result<Unit> = { _, _, _ ->
            Result.success(Unit)
        }
    ): PushService {
        val pushProvider1 = FakePushProvider(
            index = 0,
            name = "aFakePushProvider0",
            distributors = listOf(Distributor("aDistributorValue0", "aDistributorName0")),
        )
        val pushProvider2 = FakePushProvider(
            index = 1,
            name = "aFakePushProvider1",
            distributors = listOf(Distributor("aDistributorValue1", "aDistributorName1")),
        )
        return FakePushService(
            availablePushProviders = listOf(pushProvider1, pushProvider2),
            registerWithLambda = registerWithLambda,
        )
    }

    private fun TestScope.createNotificationSettingsPresenter(
        notificationSettingsService: FakeNotificationSettingsService = FakeNotificationSettingsService(),
        pushService: PushService = FakePushService(),
        fullScreenIntentPermissionsStateLambda: () -> FullScreenIntentPermissionsState = { aFullScreenIntentPermissionsState() },
    ): NotificationSettingsPresenter {
        val prismClient = FakePRISMClient(notificationSettingsService = notificationSettingsService)
        return NotificationSettingsPresenter(
            notificationSettingsService = notificationSettingsService,
            userPushStoreFactory = FakeUserPushStoreFactory(),
            prismClient = prismClient,
            pushService = pushService,
            systemNotificationsEnabledProvider = FakeSystemNotificationsEnabledProvider(),
            fullScreenIntentPermissionsPresenter = { fullScreenIntentPermissionsStateLambda() },
            sessionCoroutineScope = backgroundScope,
        )
    }
}
