/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.call.impl.utils

import dev.zacsweers.metro.Inject
import io.prism.android.features.call.impl.data.WidgetMessage
import io.prism.android.libraries.androidutils.json.JsonProvider
import io.prism.android.libraries.core.extensions.runCatchingExceptions

@Inject
class WidgetMessageSerializer(
    private val json: JsonProvider,
) {
    fun deserialize(message: String): Result<WidgetMessage> {
        return runCatchingExceptions { json().decodeFromString(WidgetMessage.serializer(), message) }
    }

    fun serialize(message: WidgetMessage): String {
        return json().encodeToString(WidgetMessage.serializer(), message)
    }
}
