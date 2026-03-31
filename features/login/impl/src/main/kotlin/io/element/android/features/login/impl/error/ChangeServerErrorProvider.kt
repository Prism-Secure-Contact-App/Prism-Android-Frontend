/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.login.impl.error

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

class ChangeServerErrorProvider : PreviewParameterProvider<ChangeServerError> {
    override val values: Sequence<ChangeServerError>
        get() = sequenceOf(
            ChangeServerError.InvalidServer,
            ChangeServerError.Error(
                messageStr = "An error description",
            ),
            ChangeServerError.NeedPRISMPro(
                unauthorisedAccountProviderTitle = "prism.io",
                applicationId = "io.prism.enterprise",
            ),
            ChangeServerError.UnauthorizedAccountProvider(
                unauthorisedAccountProviderTitle = "prism.io",
                authorisedAccountProviderTitles = listOf("provider.org", "provider.io"),
            ),
            ChangeServerError.SlidingSyncAlert,
            ChangeServerError.UnsupportedServer,
        )
}
