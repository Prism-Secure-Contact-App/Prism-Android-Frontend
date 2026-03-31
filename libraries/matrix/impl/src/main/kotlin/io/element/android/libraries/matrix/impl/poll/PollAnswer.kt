/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.prism.impl.poll

import io.prism.android.libraries.prism.api.poll.PollAnswer
import org.prism.rustcomponents.sdk.PollAnswer as RustPollAnswer

fun RustPollAnswer.map(): PollAnswer = PollAnswer(
    id = id,
    text = text,
)
