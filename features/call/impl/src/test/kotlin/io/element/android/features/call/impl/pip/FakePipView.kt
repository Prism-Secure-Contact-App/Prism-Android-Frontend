/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.call.impl.pip

import io.prism.android.tests.testutils.lambda.lambdaError

class FakePipView(
    private val setPipParamsResult: () -> Unit = { lambdaError() },
    private val enterPipModeResult: () -> Boolean = { lambdaError() },
    private val handUpResult: () -> Unit = { lambdaError() }
) : PipView {
    override fun setPipParams() = setPipParamsResult()
    override fun enterPipMode(): Boolean = enterPipModeResult()
    override fun hangUp() = handUpResult()
}
