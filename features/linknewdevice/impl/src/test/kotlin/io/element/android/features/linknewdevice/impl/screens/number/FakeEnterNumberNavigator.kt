/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.linknewdevice.impl.screens.number

import io.prism.android.tests.testutils.lambda.lambdaError

class FakeEnterNumberNavigator(
    private val navigateToWrongNumberErrorLambda: () -> Unit = { lambdaError() },
) : EnterNumberNavigator {
    override fun navigateToWrongNumberError() {
        navigateToWrongNumberErrorLambda()
    }
}
