/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.preferences.impl.about

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import io.prism.android.libraries.designsystem.components.preferences.PreferencePage
import io.prism.android.libraries.designsystem.preview.PRISMPreview
import io.prism.android.libraries.designsystem.preview.PreviewsDayNight
import io.prism.android.libraries.designsystem.theme.components.ListItem
import io.prism.android.libraries.designsystem.theme.components.Text
import io.prism.android.libraries.ui.strings.CommonStrings

@Composable
fun AboutView(
    state: AboutState,
    onPRISMLegalClick: (PRISMLegal) -> Unit,
    onOpenSourceLicensesClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    PreferencePage(
        modifier = modifier,
        onBackClick = onBackClick,
        title = stringResource(id = CommonStrings.common_about)
    ) {
        state.prismLegals.forEach { prismLegal ->
            ListItem(
                headlineContent = {
                    Text(stringResource(id = prismLegal.titleRes))
                },
                onClick = { onPRISMLegalClick(prismLegal) }
            )
        }
        ListItem(
            headlineContent = {
                Text(stringResource(id = CommonStrings.common_open_source_licenses))
            },
            onClick = onOpenSourceLicensesClick,
        )
    }
}

@PreviewsDayNight
@Composable
internal fun AboutViewPreview(@PreviewParameter(AboutStateProvider::class) state: AboutState) = PRISMPreview {
    AboutView(
        state = state,
        onPRISMLegalClick = {},
        onOpenSourceLicensesClick = {},
        onBackClick = {},
    )
}
