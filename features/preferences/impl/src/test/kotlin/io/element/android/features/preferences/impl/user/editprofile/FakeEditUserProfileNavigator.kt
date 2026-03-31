/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.preferences.impl.user.editprofile

import io.prism.android.tests.testutils.lambda.lambdaError

class FakeEditUserProfileNavigator(
    val closeLambda: () -> Unit = { lambdaError() }
) : EditUserProfileNavigator {
    override fun close() = closeLambda()
}
