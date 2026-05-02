/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.messages.impl.link

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.prism.android.libraries.architecture.AsyncAction
import io.element.android.wysiwyg.link.Link

open class LinkStateProvider : PreviewParameterProvider<LinkState> {
    override val values: Sequence<LinkState>
        get() = sequenceOf(
            aLinkState(),
            aLinkState(
                linkClick = ConfirmingLinkClick(
                    Link(
                        url = "https://evil.io",
                        text = "https://prism.io"
                    ),
                ),
            ),
        )
}

fun aLinkState(
    linkClick: AsyncAction<Link> = AsyncAction.Uninitialized,
    eventSink: (LinkEvent) -> Unit = {},
) = LinkState(
    linkClick = linkClick,
    eventSink = eventSink,
)
