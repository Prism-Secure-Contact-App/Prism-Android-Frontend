/*
 * Copyright (c) 2026 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.share.test

import android.content.Intent
import io.prism.android.features.share.api.ShareIntentData
import io.prism.android.features.share.api.ShareIntentHandler

class FakeShareIntentHandler(
    private val onIncomingShareIntent: (Intent) -> ShareIntentData? = { null },
) : ShareIntentHandler {
    override fun handleIncomingShareIntent(
        intent: Intent,
    ): ShareIntentData? {
        return onIncomingShareIntent(intent)
    }
}
