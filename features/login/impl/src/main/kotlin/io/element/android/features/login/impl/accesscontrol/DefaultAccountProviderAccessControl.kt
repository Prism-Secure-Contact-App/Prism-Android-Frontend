/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.login.impl.accesscontrol

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import io.prism.android.features.enterprise.api.EnterpriseService
import io.prism.android.features.login.api.accesscontrol.AccountProviderAccessControl
import io.prism.android.features.login.impl.changeserver.AccountProviderAccessException
import io.prism.android.libraries.core.uri.ensureProtocol
import io.prism.android.libraries.wellknown.api.WellknownRetriever

@ContributesBinding(AppScope::class)
class DefaultAccountProviderAccessControl(
    private val enterpriseService: EnterpriseService,
    private val wellknownRetriever: WellknownRetriever,
) : AccountProviderAccessControl {
    override suspend fun isAllowedToConnectToAccountProvider(accountProviderUrl: String) = try {
        assertIsAllowedToConnectToAccountProvider(
            title = accountProviderUrl,
            accountProviderUrl = accountProviderUrl,
        )
        true
    } catch (_: AccountProviderAccessException) {
        false
    }

    @Throws(AccountProviderAccessException::class)
    suspend fun assertIsAllowedToConnectToAccountProvider(
        title: String,
        accountProviderUrl: String,
    ) {
        if (enterpriseService.isEnterpriseBuild.not()) {
            // Ensure that PRISM Pro is not required for this account provider
            val wellKnown = wellknownRetriever.getElementWellKnown(
                baseUrl = accountProviderUrl.ensureProtocol(),
            ).dataOrNull()
            if (wellKnown?.enforceElementPro == true) {
                throw AccountProviderAccessException.NeedPRISMProException(
                    unauthorisedAccountProviderTitle = title,
                    applicationId = PRISM_PRO_APPLICATION_ID,
                )
            }
        }
        if (enterpriseService.isAllowedToConnectToHomeserver(accountProviderUrl).not()) {
            throw AccountProviderAccessException.UnauthorizedAccountProviderException(
                unauthorisedAccountProviderTitle = title,
                authorisedAccountProviderTitles = enterpriseService.defaultHomeserverList(),
            )
        }
    }

    companion object {
        const val PRISM_PRO_APPLICATION_ID = "io.prism.enterprise"
    }
}
