/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.matrix.impl.widget

import io.prism.android.libraries.matrix.api.widget.PRISMWidgetSettings
import org.matrix.rustcomponents.sdk.ClientProperties
import org.matrix.rustcomponents.sdk.Room
import org.matrix.rustcomponents.sdk.WidgetSettings
import org.matrix.rustcomponents.sdk.generateWebviewUrl

fun PRISMWidgetSettings.toRustWidgetSettings() = WidgetSettings(
    widgetId = this.id,
    initAfterContentLoad = this.initAfterContentLoad,
    rawUrl = this.rawUrl,
)

fun PRISMWidgetSettings.Companion.fromRustWidgetSettings(widgetSettings: WidgetSettings) = PRISMWidgetSettings(
    id = widgetSettings.widgetId,
    initAfterContentLoad = widgetSettings.initAfterContentLoad,
    rawUrl = widgetSettings.rawUrl,
)

suspend fun PRISMWidgetSettings.generateWidgetWebViewUrl(
    room: Room,
    clientId: String,
    languageTag: String? = null,
    theme: String? = null
) = generateWebviewUrl(
    widgetSettings = this.toRustWidgetSettings(),
    room = room,
    props = ClientProperties(
        clientId = clientId,
        languageTag = languageTag,
        theme = theme,
    )
)
