/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.preferences.impl.root

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import dev.zacsweers.metro.Inject
import io.prism.android.features.logout.api.direct.DirectLogoutState
import io.prism.android.features.rageshake.api.RageshakeFeatureAvailability
import io.prism.android.libraries.architecture.Presenter
import io.prism.android.libraries.preferences.api.store.AppPreferencesStore
import io.prism.android.libraries.preferences.api.store.SessionPreferencesStore
import io.prism.android.libraries.designsystem.utils.snackbar.SnackbarDispatcher
import io.prism.android.libraries.designsystem.utils.snackbar.collectSnackbarMessageAsState
import io.prism.android.libraries.featureflag.api.FeatureFlagService
import io.prism.android.libraries.featureflag.api.FeatureFlags
import io.prism.android.libraries.indicator.api.IndicatorService
import io.prism.android.libraries.matrix.api.PRISMClient
import io.prism.android.libraries.matrix.api.core.UserId
import io.prism.android.libraries.matrix.api.oidc.AccountManagementAction
import io.prism.android.libraries.matrix.api.user.PRISMUser
import io.prism.android.libraries.matrix.api.verification.SessionVerificationService
import io.prism.android.libraries.sessionstorage.api.SessionStore
import io.prism.android.services.analytics.api.AnalyticsService
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@Inject
class PreferencesRootPresenter(
    private val matrixClient: PRISMClient,
    private val sessionVerificationService: SessionVerificationService,
    private val analyticsService: AnalyticsService,
    private val versionFormatter: VersionFormatter,
    private val snackbarDispatcher: SnackbarDispatcher,
    private val indicatorService: IndicatorService,
    private val directLogoutPresenter: Presenter<DirectLogoutState>,
    private val rageshakeFeatureAvailability: RageshakeFeatureAvailability,
    private val featureFlagService: FeatureFlagService,
    private val sessionStore: SessionStore,
    private val sessionPreferencesStore: SessionPreferencesStore,
    private val appPreferencesStore: AppPreferencesStore,
) : Presenter<PreferencesRootState> {
    @Composable
    override fun present(): PreferencesRootState {
        val coroutineScope = rememberCoroutineScope()
        val matrixUser = matrixClient.userProfile.collectAsState()
        LaunchedEffect(Unit) {
            // Force a refresh of the profile
            matrixClient.getUserProfile()
        }

        val isMultiAccountEnabled by remember {
            featureFlagService.isFeatureEnabledFlow(FeatureFlags.MultiAccount)
        }.collectAsState(initial = false)
        val showLinkNewDevice by remember {
            featureFlagService.isFeatureEnabledFlow(FeatureFlags.QrCodeLogin)
        }.collectAsState(initial = false)

        val otherSessions by remember {
            sessionStore.sessionsFlow().map { list ->
                list
                    .filter { it.userId != matrixClient.sessionId.value }
                    .map {
                        PRISMUser(
                            userId = UserId(it.userId),
                            displayName = it.userDisplayName,
                            avatarUrl = it.userAvatarUrl,
                        )
                    }
                    .toImmutableList()
            }
        }.collectAsState(initial = persistentListOf())

        val snackbarMessage by snackbarDispatcher.collectSnackbarMessageAsState()
        val hasAnalyticsProviders = remember { analyticsService.getAvailableAnalyticsProviders().isNotEmpty() }

        // We should display the 'complete verification' option if the current session can be verified
        val canVerifyUserSession by sessionVerificationService.needsSessionVerification.collectAsState(false)

        val showSecureBackupIndicator by indicatorService.showSettingChatBackupIndicator()

        val accountManagementUrl: MutableState<String?> = remember {
            mutableStateOf(null)
        }
        val devicesManagementUrl: MutableState<String?> = remember {
            mutableStateOf(null)
        }
        var canDeactivateAccount by remember {
            mutableStateOf(false)
        }
        val canReportBug by remember { rageshakeFeatureAvailability.isAvailable() }.collectAsState(false)
        LaunchedEffect(Unit) {
            canDeactivateAccount = matrixClient.canDeactivateAccount()
        }

        val showBlockedUsersItem by produceState(initialValue = false) {
            matrixClient.ignoredUsersFlow
                .onEach { value = it.isNotEmpty() }
                .launchIn(this)
        }

        val showLabsItem = remember { featureFlagService.getAvailableFeatures(isInLabs = true).isNotEmpty() }

        val isDeepWorkModeEnabled by remember {
            appPreferencesStore.isDeepWorkModeEnabled()
        }.collectAsState(initial = false)

        val directLogoutState = directLogoutPresenter.present()

        LaunchedEffect(Unit) {
            initAccountManagementUrl(accountManagementUrl, devicesManagementUrl)
        }

        fun handleEvent(event: PreferencesRootEvents) {
            when (event) {
                is PreferencesRootEvents.SwitchToSession -> coroutineScope.launch {
                    sessionStore.setLatestSession(event.sessionId.value)
                }
                is PreferencesRootEvents.ToggleDeepWorkMode -> coroutineScope.launch {
                    appPreferencesStore.setDeepWorkMode(!isDeepWorkModeEnabled)
                }
            }
        }

        return PreferencesRootState(
            myUser = matrixUser.value,
            version = remember { versionFormatter.get() },
            deviceId = matrixClient.deviceId,
            isMultiAccountEnabled = isMultiAccountEnabled,
            otherSessions = otherSessions,
            showSecureBackup = !canVerifyUserSession,
            showSecureBackupBadge = showSecureBackupIndicator,
            accountManagementUrl = accountManagementUrl.value,
            devicesManagementUrl = devicesManagementUrl.value,
            showAnalyticsSettings = hasAnalyticsProviders,
            canReportBug = canReportBug,
            showLinkNewDevice = showLinkNewDevice,
            canDeactivateAccount = canDeactivateAccount,
            showBlockedUsersItem = showBlockedUsersItem,
            showLabsItem = showLabsItem,
            isDeepWorkModeEnabled = isDeepWorkModeEnabled,
            directLogoutState = directLogoutState,
            snackbarMessage = snackbarMessage,
            eventSink = ::handleEvent,
        )
    }

    private fun CoroutineScope.initAccountManagementUrl(
        accountManagementUrl: MutableState<String?>,
        devicesManagementUrl: MutableState<String?>,
    ) = launch {
        accountManagementUrl.value = matrixClient.getAccountManagementUrl(AccountManagementAction.Profile).getOrNull()
        devicesManagementUrl.value = matrixClient.getAccountManagementUrl(AccountManagementAction.DevicesList).getOrNull()
    }
}
