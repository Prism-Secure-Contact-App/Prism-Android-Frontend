/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.messages.impl.link

import io.prism.android.wysiwyg.link.Link

sealed interface LinkEvent {
    data class OnLinkClick(val link: Link) : LinkEvent
    data object Confirm : LinkEvent
    data object Cancel : LinkEvent
}
