/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.login.impl.screens.createaccount

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.prism.android.libraries.architecture.AsyncAction
import io.prism.android.libraries.prism.api.core.SessionId

open class CreateAccountStateProvider : PreviewParameterProvider<CreateAccountState> {
    override val values: Sequence<CreateAccountState>
        get() = sequenceOf(
            aCreateAccountState(),
            aCreateAccountState(pageProgress = 33),
            aCreateAccountState(createAction = AsyncAction.Loading),
            aCreateAccountState(createAction = AsyncAction.Failure(RuntimeException("Failed to create account"))),
        )
}

private fun aCreateAccountState(
    pageProgress: Int = 100,
    createAction: AsyncAction<SessionId> = AsyncAction.Uninitialized,
) = CreateAccountState(
    url = "https://example.com",
    isDebugBuild = true,
    pageProgress = pageProgress,
    createAction = createAction,
    eventSink = {}
)
