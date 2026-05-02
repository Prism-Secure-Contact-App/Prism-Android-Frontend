/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

@file:OptIn(ExperimentalCoroutinesApi::class)

package io.prism.android.features.preferences.impl.root

import app.cash.turbine.ReceiveTurbine
import com.google.common.truth.Truth.assertThat
import io.prism.android.features.logout.api.direct.aDirectLogoutState
import io.prism.android.features.preferences.impl.utils.ShowDeveloperSettingsProvider
import io.prism.android.features.rageshake.api.RageshakeFeatureAvailability
import io.prism.android.libraries.core.meta.BuildType
import io.prism.android.libraries.designsystem.utils.snackbar.SnackbarDispatcher
import io.prism.android.libraries.featureflag.api.FeatureFlagService
import io.prism.android.libraries.featureflag.api.FeatureFlags
import io.prism.android.libraries.featureflag.test.FakeFeature
import io.prism.android.libraries.featureflag.test.FakeFeatureFlagService
import io.prism.android.libraries.indicator.api.IndicatorService
import io.prism.android.libraries.indicator.test.FakeIndicatorService
import io.prism.android.libraries.matrix.api.oidc.AccountManagementAction
import io.prism.android.libraries.matrix.api.user.PRISMUser
import io.prism.android.libraries.matrix.test.AN_AVATAR_URL
import io.prism.android.libraries.matrix.test.A_SESSION_ID
import io.prism.android.libraries.matrix.test.A_SESSION_ID_2
import io.prism.android.libraries.matrix.test.A_USER_NAME
import io.prism.android.libraries.matrix.test.FakePRISMClient
import io.prism.android.libraries.matrix.test.core.aBuildMeta
import io.prism.android.libraries.matrix.test.verification.FakeSessionVerificationService
import io.prism.android.libraries.sessionstorage.api.SessionStore
import io.prism.android.libraries.sessionstorage.test.InMemorySessionStore
import io.prism.android.libraries.sessionstorage.test.aSessionData
import io.prism.android.services.analytics.test.FakeAnalyticsService
import io.prism.android.tests.testutils.WarmUpRule
import io.prism.android.tests.testutils.lambda.lambdaRecorder
import io.prism.android.tests.testutils.lambda.value
import io.prism.android.tests.testutils.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class PreferencesRootPresenterTest {
    @get:Rule
    val warmUpRule = WarmUpRule()

    @Test
    fun `present - initial state`() = runTest {
        val accountManagementUrlResult = lambdaRecorder<AccountManagementAction?, Result<String?>> { action ->
            Result.success("$action url")
        }
        val matrixClient = FakePRISMClient(
            canDeactivateAccountResult = { true },
            accountManagementUrlResult = accountManagementUrlResult,
        )
        createPresenter(
            matrixClient = matrixClient,
        ).test {
            val initialState = awaitItem()
            assertThat(initialState.myUser).isEqualTo(
                PRISMUser(
                    userId = matrixClient.sessionId,
                    displayName = A_USER_NAME,
                    avatarUrl = AN_AVATAR_URL
                )
            )
            assertThat(initialState.version).isEqualTo("A Version")
            assertThat(initialState.isMultiAccountEnabled).isFalse()
            assertThat(initialState.otherSessions).isEmpty()
            val loadedState = awaitItem()
            assertThat(loadedState.myUser).isEqualTo(
                PRISMUser(
                    userId = matrixClient.sessionId,
                    displayName = A_USER_NAME,
                    avatarUrl = AN_AVATAR_URL
                )
            )
            assertThat(initialState.version).isEqualTo("A Version")
            assertThat(loadedState.showSecureBackup).isFalse()
            assertThat(loadedState.showSecureBackupBadge).isFalse()
            assertThat(loadedState.accountManagementUrl).isNull()
            assertThat(loadedState.devicesManagementUrl).isNull()
            assertThat(loadedState.showAnalyticsSettings).isFalse()
            assertThat(loadedState.showLinkNewDevice).isFalse()
            assertThat(loadedState.showDeveloperSettings).isTrue()
            assertThat(loadedState.canDeactivateAccount).isTrue()
            assertThat(loadedState.canReportBug).isTrue()
            assertThat(loadedState.directLogoutState).isEqualTo(aDirectLogoutState())
            assertThat(loadedState.snackbarMessage).isNull()
            skipItems(1)
            val finalState = awaitItem()
            accountManagementUrlResult.assertions().isCalledExactly(2)
                .withSequence(
                    listOf(value(AccountManagementAction.Profile)),
                    listOf(value(AccountManagementAction.DevicesList)),
                )
            assertThat(finalState.accountManagementUrl).isEqualTo("Profile url")
            assertThat(finalState.devicesManagementUrl).isEqualTo("DevicesList url")
        }
    }

    @Test
    fun `present - cannot report bug`() = runTest {
        val matrixClient = FakePRISMClient(
            canDeactivateAccountResult = { true },
            accountManagementUrlResult = { Result.success("") },
        )
        createPresenter(
            matrixClient = matrixClient,
            rageshakeFeatureAvailability = { flowOf(false) },
        ).test {
            val initialState = awaitItem()
            assertThat(initialState.canReportBug).isFalse()
            skipItems(1)
        }
    }

    @Test
    fun `present - secure backup badge`() = runTest {
        val matrixClient = FakePRISMClient(
            canDeactivateAccountResult = { true },
            accountManagementUrlResult = { Result.success("") },
        )
        val indicatorService = FakeIndicatorService()
        createPresenter(
            matrixClient = matrixClient,
            rageshakeFeatureAvailability = { flowOf(false) },
            indicatorService = indicatorService,
        ).test {
            skipItems(1)
            val initialState = awaitItem()
            assertThat(initialState.showSecureBackupBadge).isFalse()
            indicatorService.setShowSettingChatBackupIndicator(true)
            val finalState = awaitItem()
            assertThat(finalState.showSecureBackupBadge).isTrue()
        }
    }

    @Test
    fun `present - can deactivate account is false if the PRISM client say so`() = runTest {
        createPresenter(
            matrixClient = FakePRISMClient(
                canDeactivateAccountResult = { false },
                accountManagementUrlResult = { Result.success(null) },
            ),
        ).test {
            val loadedState = awaitFirstItem()
            assertThat(loadedState.canDeactivateAccount).isFalse()
        }
    }

    @Test
    fun `present - developer settings is hidden by default in release builds`() = runTest {
        createPresenter(
            matrixClient = FakePRISMClient(
                canDeactivateAccountResult = { true },
                accountManagementUrlResult = { Result.success(null) },
            ),
            showDeveloperSettingsProvider = ShowDeveloperSettingsProvider(aBuildMeta(BuildType.RELEASE))
        ).test {
            val loadedState = awaitFirstItem()
            assertThat(loadedState.showDeveloperSettings).isFalse()
        }
    }

    @Test
    fun `present - developer settings can be enabled in release builds`() = runTest {
        createPresenter(
            matrixClient = FakePRISMClient(
                canDeactivateAccountResult = { true },
                accountManagementUrlResult = { Result.success(null) },
            ),
            showDeveloperSettingsProvider = ShowDeveloperSettingsProvider(aBuildMeta(BuildType.RELEASE))
        ).test {
            val loadedState = awaitFirstItem()
            repeat(times = ShowDeveloperSettingsProvider.DEVELOPER_SETTINGS_COUNTER) {
                assertThat(loadedState.showDeveloperSettings).isFalse()
                loadedState.eventSink(PreferencesRootEvents.OnVersionInfoClick)
            }
            assertThat(awaitItem().showDeveloperSettings).isTrue()
        }
    }

    @Test
    fun `present - labs can be shown if any feature flag is in labs and not finished`() = runTest {
        createPresenter(
            featureFlagService = FakeFeatureFlagService(
                getAvailableFeaturesResult = { _, _ ->
                    listOf(
                        FakeFeature(
                            key = "feature_1",
                            title = "Feature 1",
                            isInLabs = true,
                            isFinished = false,
                        )
                    )
                }
            ),
            matrixClient = FakePRISMClient(
                canDeactivateAccountResult = { true },
                accountManagementUrlResult = { Result.success(null) },
            ),
        ).test {
            assertThat(awaitItem().showLabsItem).isTrue()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `present - labs can't be shown if all feature flags in labs are finished`() = runTest {
        createPresenter(
            featureFlagService = FakeFeatureFlagService(
                getAvailableFeaturesResult = { _, _ ->
                    emptyList()
                }
            ),
            matrixClient = FakePRISMClient(
                canDeactivateAccountResult = { true },
                accountManagementUrlResult = { Result.success(null) },
            ),
        ).test {
            skipItems(1)
            assertThat(awaitItem().showLabsItem).isFalse()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `present - multiple accounts`() = runTest {
        createPresenter(
            matrixClient = FakePRISMClient(
                sessionId = A_SESSION_ID,
                canDeactivateAccountResult = { true },
            ),
            featureFlagService = FakeFeatureFlagService(
                initialState = mapOf(FeatureFlags.MultiAccount.key to true)
            ),
            sessionStore = InMemorySessionStore(
                initialList = listOf(
                    aSessionData(sessionId = A_SESSION_ID.value),
                    aSessionData(
                        sessionId = A_SESSION_ID_2.value,
                        userDisplayName = "Bob",
                        userAvatarUrl = "avatarUrl",
                    ),
                )
            )
        ).test {
            val state = awaitFirstItem()
            assertThat(state.isMultiAccountEnabled).isTrue()
            assertThat(state.otherSessions).hasSize(1)
            assertThat(state.otherSessions[0]).isEqualTo(PRISMUser(userId = A_SESSION_ID_2, displayName = "Bob", avatarUrl = "avatarUrl"))
        }
    }

    @Test
    fun `present - link new device`() = runTest {
        createPresenter(
            matrixClient = FakePRISMClient(
                sessionId = A_SESSION_ID,
                canDeactivateAccountResult = { true },
            ),
            featureFlagService = FakeFeatureFlagService(
                initialState = mapOf(FeatureFlags.QrCodeLogin.key to true)
            ),
        ).test {
            val state = awaitFirstItem()
            assertThat(state.showLinkNewDevice).isTrue()
        }
    }

    private suspend fun <T> ReceiveTurbine<T>.awaitFirstItem(): T {
        skipItems(1)
        return awaitItem()
    }

    private fun createPresenter(
        matrixClient: FakePRISMClient = FakePRISMClient(),
        sessionVerificationService: FakeSessionVerificationService = FakeSessionVerificationService(),
        showDeveloperSettingsProvider: ShowDeveloperSettingsProvider = ShowDeveloperSettingsProvider(aBuildMeta(BuildType.DEBUG)),
        rageshakeFeatureAvailability: RageshakeFeatureAvailability = RageshakeFeatureAvailability { flowOf(true) },
        indicatorService: IndicatorService = FakeIndicatorService(),
        featureFlagService: FeatureFlagService = FakeFeatureFlagService(),
        sessionStore: SessionStore = InMemorySessionStore(),
    ) = PreferencesRootPresenter(
        matrixClient = matrixClient,
        sessionVerificationService = sessionVerificationService,
        analyticsService = FakeAnalyticsService(),
        versionFormatter = FakeVersionFormatter(),
        snackbarDispatcher = SnackbarDispatcher(),
        indicatorService = indicatorService,
        directLogoutPresenter = { aDirectLogoutState() },
        showDeveloperSettingsProvider = showDeveloperSettingsProvider,
        rageshakeFeatureAvailability = rageshakeFeatureAvailability,
        featureFlagService = featureFlagService,
        sessionStore = sessionStore,
    )
}
