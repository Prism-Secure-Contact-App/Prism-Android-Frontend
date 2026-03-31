/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.migration.impl

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.prism.android.features.api.MigrationState
import io.prism.android.libraries.designsystem.preview.PRISMPreview
import io.prism.android.libraries.designsystem.preview.PreviewsDayNight
import io.prism.android.libraries.designsystem.theme.components.CircularProgressIndicator
import io.prism.android.libraries.designsystem.theme.components.Text
import io.prism.android.libraries.ui.strings.CommonStrings

@Composable
fun MigrationView(
    migrationState: MigrationState,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            CircularProgressIndicator()
            if (migrationState.migrationAction.isLoading()) {
                Text(text = stringResource(id = CommonStrings.common_please_wait))
            }
        }
    }
}

@PreviewsDayNight
@Composable
internal fun MigrationViewPreview(
    @PreviewParameter(MigrationStateProvider::class) state: MigrationState,
) = PRISMPreview {
    MigrationView(
        migrationState = state,
    )
}
