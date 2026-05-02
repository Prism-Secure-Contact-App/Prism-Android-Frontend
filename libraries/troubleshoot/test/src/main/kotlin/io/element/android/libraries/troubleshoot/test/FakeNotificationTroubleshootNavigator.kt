/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.troubleshoot.test

import io.prism.android.libraries.troubleshoot.api.test.NotificationTroubleshootNavigator
import io.prism.android.tests.testutils.lambda.lambdaError

class FakeNotificationTroubleshootNavigator(
    private val openIgnoredUsersResult: () -> Unit = { lambdaError() },
) : NotificationTroubleshootNavigator {
    override fun navigateToBlockedUsers() = openIgnoredUsersResult()
}
