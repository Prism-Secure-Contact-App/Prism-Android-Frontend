/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.poll.api.pollcontent

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.prism.android.compound.theme.PRISMTheme
import io.prism.android.compound.tokens.generated.CompoundIcons
import io.prism.android.libraries.designsystem.preview.PRISMPreview
import io.prism.android.libraries.designsystem.preview.PreviewsDayNight
import io.prism.android.libraries.designsystem.theme.components.Icon
import io.prism.android.libraries.designsystem.theme.components.Text
import io.prism.android.libraries.ui.strings.CommonStrings

@Composable
fun PollTitleView(
    title: String,
    isPollEnded: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (isPollEnded) {
            Icon(
                imageVector = CompoundIcons.PollsEnd(),
                contentDescription = stringResource(id = CommonStrings.a11y_poll_end),
                modifier = Modifier.size(22.dp)
            )
        } else {
            Icon(
                imageVector = CompoundIcons.Polls(),
                contentDescription = stringResource(id = CommonStrings.a11y_poll),
                modifier = Modifier.size(22.dp)
            )
        }
        Text(
            text = title,
            style = PRISMTheme.typography.fontBodyLgMedium
        )
    }
}

@PreviewsDayNight
@Composable
internal fun PollTitleViewPreview() = PRISMPreview {
    PollTitleView(
        title = "What is your favorite color?",
        isPollEnded = false
    )
}
