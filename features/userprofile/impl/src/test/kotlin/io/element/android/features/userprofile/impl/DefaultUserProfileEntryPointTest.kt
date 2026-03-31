/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.userprofile.impl

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.testing.junit4.util.MainDispatcherRule
import com.google.common.truth.Truth.assertThat
import io.prism.android.features.call.test.FakePRISMCallEntryPoint
import io.prism.android.features.userprofile.api.UserProfileEntryPoint
import io.prism.android.features.verifysession.test.FakeOutgoingVerificationEntryPoint
import io.prism.android.libraries.prism.api.core.RoomId
import io.prism.android.libraries.prism.test.A_SESSION_ID
import io.prism.android.libraries.prism.test.A_USER_ID
import io.prism.android.libraries.mediaviewer.test.FakeMediaViewerEntryPoint
import io.prism.android.tests.testutils.lambda.lambdaError
import io.prism.android.tests.testutils.node.TestParentNode
import org.junit.Rule
import org.junit.Test

class DefaultUserProfileEntryPointTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `test node builder`() {
        val entryPoint = DefaultUserProfileEntryPoint()

        val parentNode = TestParentNode.create { buildContext, plugins ->
            UserProfileFlowNode(
                buildContext = buildContext,
                plugins = plugins,
                sessionId = A_SESSION_ID,
                prismCallEntryPoint = FakePRISMCallEntryPoint(),
                mediaViewerEntryPoint = FakeMediaViewerEntryPoint(),
                outgoingVerificationEntryPoint = FakeOutgoingVerificationEntryPoint(),
            )
        }
        val callback = object : UserProfileEntryPoint.Callback {
            override fun navigateToRoom(roomId: RoomId) {
                lambdaError()
            }
        }
        val params = UserProfileEntryPoint.Params(
            userId = A_USER_ID,
        )
        val result = entryPoint.createNode(
            parentNode = parentNode,
            buildContext = BuildContext.root(null),
            params = params,
            callback = callback,
        )
        assertThat(result).isInstanceOf(UserProfileFlowNode::class.java)
        assertThat(result.plugins).contains(params)
        assertThat(result.plugins).contains(callback)
    }
}
