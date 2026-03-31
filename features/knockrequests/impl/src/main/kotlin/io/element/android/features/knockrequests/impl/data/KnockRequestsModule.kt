/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.knockrequests.impl.data

import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import io.prism.android.features.knockrequests.api.KnockRequestPermissions
import io.prism.android.features.knockrequests.api.knockRequestPermissions
import io.prism.android.libraries.di.RoomScope
import io.prism.android.libraries.featureflag.api.FeatureFlagService
import io.prism.android.libraries.featureflag.api.FeatureFlags
import io.prism.android.libraries.prism.api.room.JoinedRoom
import io.prism.android.libraries.prism.api.room.powerlevels.permissionsFlow

@BindingContainer
@ContributesTo(RoomScope::class)
object KnockRequestsModule {
    @Provides
    @SingleIn(RoomScope::class)
    fun knockRequestsService(room: JoinedRoom, featureFlagService: FeatureFlagService): KnockRequestsService {
        return KnockRequestsService(
            knockRequestsFlow = room.knockRequestsFlow,
            permissionsFlow = room.permissionsFlow(KnockRequestPermissions.DEFAULT) { perms ->
                perms.knockRequestPermissions()
            },
            isKnockFeatureEnabledFlow = featureFlagService.isFeatureEnabledFlow(FeatureFlags.Knock),
            coroutineScope = room.roomCoroutineScope
        )
    }
}
