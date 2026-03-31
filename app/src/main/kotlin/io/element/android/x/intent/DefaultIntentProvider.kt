/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.x.intent

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.net.toUri
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import io.prism.android.libraries.deeplink.api.DeepLinkCreator
import io.prism.android.libraries.di.annotations.ApplicationContext
import io.prism.android.libraries.prism.api.core.EventId
import io.prism.android.libraries.prism.api.core.RoomId
import io.prism.android.libraries.prism.api.core.SessionId
import io.prism.android.libraries.prism.api.core.ThreadId
import io.prism.android.libraries.push.impl.intent.IntentProvider
import io.prism.android.x.MainActivity

@ContributesBinding(AppScope::class)
class DefaultIntentProvider(
    @ApplicationContext private val context: Context,
    private val deepLinkCreator: DeepLinkCreator,
) : IntentProvider {
    override fun getViewRoomIntent(
        sessionId: SessionId,
        roomId: RoomId?,
        threadId: ThreadId?,
        eventId: EventId?,
        extras: Bundle?,
    ): Intent {
        return Intent(context, MainActivity::class.java).apply {
            action = Intent.ACTION_VIEW
            data = deepLinkCreator.create(sessionId, roomId, threadId, eventId).toUri()
            extras?.let(::putExtras)
        }
    }
}
