/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.messages.impl.timeline.focus

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.prism.android.features.messages.impl.timeline.FocusRequestState
import io.prism.android.libraries.matrix.api.core.EventId
import io.prism.android.libraries.matrix.api.room.errors.FocusEventException

open class FocusRequestStateProvider : PreviewParameterProvider<FocusRequestState> {
    override val values: Sequence<FocusRequestState>
        get() = sequenceOf(
            FocusRequestState.Loading(
                eventId = EventId("\$anEventId"),
            ),
            FocusRequestState.Failure(
                FocusEventException.EventNotFound(
                    eventId = EventId("\$anEventId"),
                )
            ),
            FocusRequestState.Failure(
                FocusEventException.InvalidEventId(
                    eventId = "invalid",
                    err = "An error"
                )
            ),
            FocusRequestState.Failure(
                FocusEventException.Other(
                    msg = "An error"
                )
            ),
        )
}
