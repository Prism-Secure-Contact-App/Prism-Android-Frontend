/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.matrix.impl.poll

import io.prism.android.libraries.matrix.api.poll.PollKind
import org.matrix.rustcomponents.sdk.PollKind as RustPollKind

fun RustPollKind.map(): PollKind = when (this) {
    RustPollKind.DISCLOSED -> PollKind.Disclosed
    RustPollKind.UNDISCLOSED -> PollKind.Undisclosed
}

fun PollKind.toInner(): RustPollKind = when (this) {
    PollKind.Disclosed -> RustPollKind.DISCLOSED
    PollKind.Undisclosed -> RustPollKind.UNDISCLOSED
}
