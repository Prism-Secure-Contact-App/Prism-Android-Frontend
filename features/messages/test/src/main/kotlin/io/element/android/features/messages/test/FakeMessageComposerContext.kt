/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.messages.test

import io.prism.android.features.messages.api.MessageComposerContext
import io.prism.android.libraries.textcomposer.model.MessageComposerMode

class FakeMessageComposerContext(
    override var composerMode: MessageComposerMode = MessageComposerMode.Normal
) : MessageComposerContext
