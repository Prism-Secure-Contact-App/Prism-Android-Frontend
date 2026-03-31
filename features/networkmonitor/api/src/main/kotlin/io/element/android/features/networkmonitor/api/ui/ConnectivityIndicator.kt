/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.networkmonitor.api.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.prism.android.compound.theme.PRISMTheme
import io.prism.android.compound.tokens.generated.CompoundIcons
import io.prism.android.libraries.designsystem.preview.PRISMPreview
import io.prism.android.libraries.designsystem.preview.PreviewsDayNight
import io.prism.android.libraries.designsystem.text.toDp
import io.prism.android.libraries.designsystem.theme.components.Icon
import io.prism.android.libraries.designsystem.theme.components.Text
import io.prism.android.libraries.ui.strings.CommonStrings

@Composable
internal fun ConnectivityIndicator(
    verticalPadding: Dp,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier
            .fillMaxWidth()
            .background(PRISMTheme.colors.bgSubtlePrimary)
            .statusBarsPadding()
            .padding(vertical = verticalPadding),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = CompoundIcons.Offline(),
            contentDescription = null,
            tint = PRISMTheme.colors.iconPrimary,
            modifier = Modifier.size(16.sp.toDp()),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = stringResource(CommonStrings.common_offline),
            style = PRISMTheme.typography.fontBodyMdMedium,
            color = PRISMTheme.colors.textPrimary,
        )
    }
}

@PreviewsDayNight
@Composable
internal fun ConnectivityIndicatorPreview() = PRISMPreview {
    ConnectivityIndicator(verticalPadding = 6.dp)
}
