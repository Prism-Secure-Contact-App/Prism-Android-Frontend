/*
 * Copyright (c) 2026 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.login.impl.screens.onboarding.classic

import io.prism.android.tests.testutils.lambda.lambdaError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakePRISMClassicConnection(
    private val startResult: () -> Unit = { lambdaError() },
    private val stopResult: () -> Unit = { lambdaError() },
    private val requestDataResult: () -> Unit = { lambdaError() },
    initialState: PRISMClassicConnectionState = PRISMClassicConnectionState.Idle
) : PRISMClassicConnection {
    override fun start() = startResult()
    override fun stop() = stopResult()
    override fun requestData() = requestDataResult()
    private val mutableStateFlow = MutableStateFlow(initialState)
    override val stateFlow: StateFlow<PRISMClassicConnectionState> = mutableStateFlow.asStateFlow()
    suspend fun emitState(state: PRISMClassicConnectionState) {
        mutableStateFlow.emit(state)
    }
}
