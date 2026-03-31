/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.prism.impl.permalink

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import io.prism.android.libraries.core.extensions.runCatchingExceptions
import io.prism.android.libraries.prism.api.core.PRISMPatterns
import io.prism.android.libraries.prism.api.core.RoomAlias
import io.prism.android.libraries.prism.api.core.UserId
import io.prism.android.libraries.prism.api.permalink.PermalinkBuilder
import io.prism.android.libraries.prism.api.permalink.PermalinkBuilderError
import org.prism.rustcomponents.sdk.prismToRoomAliasPermalink
import org.prism.rustcomponents.sdk.prismToUserPermalink

@ContributesBinding(AppScope::class)
class DefaultPermalinkBuilder : PermalinkBuilder {
    override fun permalinkForUser(userId: UserId): Result<String> {
        if (!PRISMPatterns.isUserId(userId.value)) {
            return Result.failure(PermalinkBuilderError.InvalidData)
        }
        return runCatchingExceptions {
            prismToUserPermalink(userId.value)
        }
    }

    override fun permalinkForRoomAlias(roomAlias: RoomAlias): Result<String> {
        if (!PRISMPatterns.isRoomAlias(roomAlias.value)) {
            return Result.failure(PermalinkBuilderError.InvalidData)
        }
        return runCatchingExceptions {
            prismToRoomAliasPermalink(roomAlias.value)
        }
    }
}
