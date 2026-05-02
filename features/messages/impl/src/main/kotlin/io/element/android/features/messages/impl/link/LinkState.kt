/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.messages.impl.link

import io.prism.android.libraries.architecture.AsyncAction
import io.element.android.wysiwyg.link.Link

data class LinkState(
    val linkClick: AsyncAction<Link>,
    val eventSink: (LinkEvent) -> Unit,
)
