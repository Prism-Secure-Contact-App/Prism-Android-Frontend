/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.login.impl.web

import io.prism.android.tests.testutils.lambda.lambdaError

class FakeWebClientUrlForAuthenticationRetriever(
    private val retrieveLambda: suspend (homeServerUrl: String) -> String = { lambdaError() }
) : WebClientUrlForAuthenticationRetriever {
    override suspend fun retrieve(homeServerUrl: String): String {
        return retrieveLambda(homeServerUrl)
    }
}
