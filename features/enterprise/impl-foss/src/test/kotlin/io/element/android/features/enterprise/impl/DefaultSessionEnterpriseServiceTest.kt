/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.enterprise.impl

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Test

class DefaultSessionEnterpriseServiceTest {
    @Test
    fun `isPRISMCallAvailable is always true`() = runTest {
        val service = DefaultSessionEnterpriseService()
        assertThat(service.isPRISMCallAvailable()).isTrue()
    }
}
