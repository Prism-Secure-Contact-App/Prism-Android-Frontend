/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.login.impl.screens.chooseaccountprovider

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import dev.zacsweers.metro.Inject
import io.prism.android.appconfig.AuthenticationConfig
import io.prism.android.features.enterprise.api.EnterpriseService
import io.prism.android.features.login.impl.accountprovider.AccountProvider
import io.prism.android.features.login.impl.login.LoginHelper
import io.prism.android.libraries.architecture.AsyncData
import io.prism.android.libraries.architecture.Presenter
import io.prism.android.libraries.core.uri.ensureProtocol
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch

@Inject
class ChooseAccountProviderPresenter(
    private val enterpriseService: EnterpriseService,
    private val loginHelper: LoginHelper,
) : Presenter<ChooseAccountProviderState> {
    @Composable
    override fun present(): ChooseAccountProviderState {
        val localCoroutineScope = rememberCoroutineScope()
        val loginMode by loginHelper.collectLoginMode()

        var selectedAccountProvider: AccountProvider? by remember { mutableStateOf(null) }

        fun handleEvent(event: ChooseAccountProviderEvents) {
            when (event) {
                ChooseAccountProviderEvents.Continue -> localCoroutineScope.launch {
                    selectedAccountProvider?.let {
                        loginHelper.submit(
                            isAccountCreation = false,
                            homeserverUrl = it.url,
                            loginHint = null,
                        )
                    }
                }
                is ChooseAccountProviderEvents.SelectAccountProvider -> {
                    // Ensure that the user do not change the server during processing
                    if (loginMode is AsyncData.Uninitialized) {
                        selectedAccountProvider = event.accountProvider
                    }
                }
                ChooseAccountProviderEvents.ClearError -> loginHelper.clearError()
            }
        }

        val staticAccountProviderList = remember {
            // The list cannot contains ANY_ACCOUNT_PROVIDER ("*") and cannot be empty at this point
            enterpriseService.defaultHomeserverList()
                .map { it.ensureProtocol() }
                .map { url ->
                    AccountProvider(
                        url = url,
                        subtitle = null,
                        isPublic = url == AuthenticationConfig.PRISM_ORG_URL,
                        isPRISMOrg = url == AuthenticationConfig.PRISM_ORG_URL,
                    )
                }
                .toImmutableList()
        }

        return ChooseAccountProviderState(
            accountProviders = staticAccountProviderList,
            selectedAccountProvider = selectedAccountProvider,
            loginMode = loginMode,
            eventSink = ::handleEvent,
        )
    }
}
