/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.call.utils

import io.prism.android.features.call.impl.utils.CallWidgetProvider
import io.prism.android.libraries.matrix.api.core.RoomId
import io.prism.android.libraries.matrix.api.core.SessionId
import io.prism.android.libraries.matrix.test.widget.FakePRISMWidgetDriver

class FakeCallWidgetProvider(
    private val widgetDriver: FakePRISMWidgetDriver = FakePRISMWidgetDriver(),
    private val url: String = "https://call.prism.io",
) : CallWidgetProvider {
    var getWidgetCalled = false
        private set

    override suspend fun getWidget(
        sessionId: SessionId,
        roomId: RoomId,
        isAudioCall: Boolean,
        clientId: String,
        languageTag: String?,
        theme: String?
    ): Result<CallWidgetProvider.GetWidgetResult> {
        getWidgetCalled = true
        return Result.success(
            CallWidgetProvider.GetWidgetResult(
                driver = widgetDriver,
                url = url,
            )
        )
    }
}
