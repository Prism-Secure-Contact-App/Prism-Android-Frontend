/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.login.impl.screens.createaccount

import io.prism.android.libraries.prism.api.auth.external.ExternalSession
import io.prism.android.tests.testutils.lambda.lambdaError

class FakeMessageParser(
    private val parseResult: (String) -> ExternalSession = { lambdaError() }
) : MessageParser {
    override fun parse(message: String): ExternalSession {
        return parseResult(message)
    }
}
