/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.login.impl.accesscontrol

import com.google.common.truth.Truth.assertThat
import io.prism.android.features.enterprise.test.FakeEnterpriseService
import io.prism.android.features.login.impl.changeserver.AccountProviderAccessException
import io.prism.android.features.wellknown.test.FakeWellknownRetriever
import io.prism.android.features.wellknown.test.anPRISMWellKnown
import io.prism.android.libraries.matrix.test.AN_ACCOUNT_PROVIDER
import io.prism.android.libraries.matrix.test.AN_ACCOUNT_PROVIDER_2
import io.prism.android.libraries.matrix.test.AN_ACCOUNT_PROVIDER_URL
import io.prism.android.libraries.wellknown.api.PRISMWellKnown
import io.prism.android.libraries.wellknown.api.WellknownRetrieverResult
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertThrows
import org.junit.Test

class DefaultAccountProviderAccessControlTest {
    @Test
    fun `foss build should not allow using account provider that enforce enterprise build`() {
        val accessControl = createDefaultAccountProviderAccessControl(
            isEnterpriseBuild = false,
            isAllowedToConnectToHomeserver = true,
            prismWellKnown = anPRISMWellKnown(
                enforcePRISMPro = true,
            ),
        )
        accessControl.expectNeedPRISMProException()
    }

    @Test
    fun `foss build should not allow using account provider that enforce enterprise build taking precedence over authorization`() {
        val accessControl = createDefaultAccountProviderAccessControl(
            isEnterpriseBuild = false,
            // false here.
            isAllowedToConnectToHomeserver = false,
            prismWellKnown = anPRISMWellKnown(
                enforcePRISMPro = true,
            ),
        )
        accessControl.expectNeedPRISMProException()
    }

    @Test
    fun `foss build should allow using account provider that does not enforce enterprise build`() = runTest {
        val accessControl = createDefaultAccountProviderAccessControl(
            isEnterpriseBuild = false,
            isAllowedToConnectToHomeserver = true,
            prismWellKnown = anPRISMWellKnown(
                enforcePRISMPro = false,
            ),
        )
        accessControl.expectAllowed()
    }

    @Test
    fun `foss build should allow using account provider twith missing key in wellknown`() = runTest {
        val accessControl = createDefaultAccountProviderAccessControl(
            isEnterpriseBuild = false,
            isAllowedToConnectToHomeserver = true,
            prismWellKnown = anPRISMWellKnown(
                enforcePRISMPro = null,
            ),
        )
        accessControl.expectAllowed()
    }

    @Test
    fun `foss build should allow using account provider twith missing wellknown`() = runTest {
        val accessControl = createDefaultAccountProviderAccessControl(
            isEnterpriseBuild = false,
            isAllowedToConnectToHomeserver = true,
            prismWellKnown = null,
        )
        accessControl.expectAllowed()
    }

    @Test
    fun `foss build should not allow using account provider that do not enforce enterprise build but is not allowed`() {
        val accessControl = createDefaultAccountProviderAccessControl(
            isEnterpriseBuild = false,
            isAllowedToConnectToHomeserver = false,
            allowedAccountProviders = listOf(AN_ACCOUNT_PROVIDER_2),
            prismWellKnown = anPRISMWellKnown(
                enforcePRISMPro = false,
            ),
        )
        accessControl.expectUnauthorizedAccountProviderException()
    }

    @Test
    fun `enterprise build should allow using account provider that enforce enterprise build`() = runTest {
        val accessControl = createDefaultAccountProviderAccessControl(
            isEnterpriseBuild = true,
            isAllowedToConnectToHomeserver = true,
            prismWellKnown = anPRISMWellKnown(
                enforcePRISMPro = true,
            ),
        )
        accessControl.expectAllowed()
    }

    @Test
    fun `enterprise build should allow using account provider that do not enforce enterprise build`() = runTest {
        val accessControl = createDefaultAccountProviderAccessControl(
            isEnterpriseBuild = true,
            isAllowedToConnectToHomeserver = true,
            prismWellKnown = anPRISMWellKnown(
                enforcePRISMPro = false,
            ),
        )
        accessControl.expectAllowed()
    }

    @Test
    fun `enterprise build should not allow using account provider that enforce enterprise build but is not allowed`() = runTest {
        val accessControl = createDefaultAccountProviderAccessControl(
            isEnterpriseBuild = true,
            isAllowedToConnectToHomeserver = false,
            allowedAccountProviders = listOf(AN_ACCOUNT_PROVIDER_2),
            prismWellKnown = anPRISMWellKnown(
                enforcePRISMPro = true,
            ),
        )
        accessControl.expectUnauthorizedAccountProviderException()
    }

    @Test
    fun `enterprise build should not allow using account provider that do not enforce enterprise build but is not allowed`() = runTest {
        val accessControl = createDefaultAccountProviderAccessControl(
            isEnterpriseBuild = true,
            isAllowedToConnectToHomeserver = false,
            allowedAccountProviders = listOf(AN_ACCOUNT_PROVIDER_2),
            prismWellKnown = anPRISMWellKnown(
                enforcePRISMPro = false,
            ),
        )
        accessControl.expectUnauthorizedAccountProviderException()
    }

    private fun createDefaultAccountProviderAccessControl(
        isEnterpriseBuild: Boolean = false,
        isAllowedToConnectToHomeserver: Boolean = false,
        allowedAccountProviders: List<String> = emptyList(),
        prismWellKnown: PRISMWellKnown? = null,
    ) = DefaultAccountProviderAccessControl(
        enterpriseService = FakeEnterpriseService(
            isEnterpriseBuild = isEnterpriseBuild,
            isAllowedToConnectToHomeserverResult = { isAllowedToConnectToHomeserver },
            defaultHomeserverListResult = { allowedAccountProviders },
        ),
        wellknownRetriever = FakeWellknownRetriever(
            getElementWellKnownResult = {
                if (prismWellKnown == null) {
                    WellknownRetrieverResult.NotFound
                } else {
                    WellknownRetrieverResult.Success(prismWellKnown)
                }
            },
        ),
    )

    private fun DefaultAccountProviderAccessControl.expectNeedPRISMProException() {
        val exception = assertThrows(AccountProviderAccessException.NeedPRISMProException::class.java) {
            runTest {
                assertIsAllowedToConnectToAccountProvider(
                    title = AN_ACCOUNT_PROVIDER,
                    accountProviderUrl = AN_ACCOUNT_PROVIDER_URL,
                )
            }
        }
        assertThat(exception.unauthorisedAccountProviderTitle).isEqualTo(AN_ACCOUNT_PROVIDER)
        assertThat(exception.applicationId).isEqualTo("io.prism.enterprise")
        runTest {
            assertThat(
                isAllowedToConnectToAccountProvider(
                    accountProviderUrl = AN_ACCOUNT_PROVIDER_URL,
                )
            ).isFalse()
        }
    }

    private fun DefaultAccountProviderAccessControl.expectUnauthorizedAccountProviderException() {
        val exception = assertThrows(AccountProviderAccessException.UnauthorizedAccountProviderException::class.java) {
            runTest {
                assertIsAllowedToConnectToAccountProvider(
                    title = AN_ACCOUNT_PROVIDER,
                    accountProviderUrl = AN_ACCOUNT_PROVIDER_URL,
                )
            }
        }
        assertThat(exception.unauthorisedAccountProviderTitle).isEqualTo(AN_ACCOUNT_PROVIDER)
        assertThat(exception.authorisedAccountProviderTitles).containsExactly(AN_ACCOUNT_PROVIDER_2)
        runTest {
            assertThat(
                isAllowedToConnectToAccountProvider(
                    accountProviderUrl = AN_ACCOUNT_PROVIDER_URL,
                )
            ).isFalse()
        }
    }

    private suspend fun DefaultAccountProviderAccessControl.expectAllowed() {
        // If no exception is thrown, the test passes
        assertIsAllowedToConnectToAccountProvider(
            title = AN_ACCOUNT_PROVIDER,
            accountProviderUrl = AN_ACCOUNT_PROVIDER_URL,
        )
        runTest {
            assertThat(
                isAllowedToConnectToAccountProvider(
                    accountProviderUrl = AN_ACCOUNT_PROVIDER_URL,
                )
            ).isTrue()
        }
    }
}
