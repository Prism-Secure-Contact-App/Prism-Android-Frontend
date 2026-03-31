/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.deeplink.impl.usecase

import android.app.Activity
import dev.zacsweers.metro.ContributesBinding
import io.prism.android.libraries.androidutils.system.startSharePlainTextIntent
import io.prism.android.libraries.core.meta.BuildMeta
import io.prism.android.libraries.deeplink.api.usecase.InviteFriendsUseCase
import io.prism.android.libraries.di.SessionScope
import io.prism.android.libraries.prism.api.PRISMClient
import io.prism.android.libraries.prism.api.permalink.PermalinkBuilder
import io.prism.android.libraries.ui.strings.CommonStrings
import io.prism.android.services.toolbox.api.strings.StringProvider
import timber.log.Timber
import io.prism.android.libraries.androidutils.R as AndroidUtilsR

@ContributesBinding(SessionScope::class)
class DefaultInviteFriendsUseCase(
    private val stringProvider: StringProvider,
    private val prismClient: PRISMClient,
    private val buildMeta: BuildMeta,
    private val permalinkBuilder: PermalinkBuilder,
) : InviteFriendsUseCase {
    override fun execute(activity: Activity) {
        val permalinkResult = permalinkBuilder.permalinkForUser(prismClient.sessionId)
        permalinkResult.fold(
            onSuccess = { permalink ->
                val appName = buildMeta.applicationName
                activity.startSharePlainTextIntent(
                    activityResultLauncher = null,
                    chooserTitle = stringProvider.getString(CommonStrings.action_invite_friends),
                    text = stringProvider.getString(CommonStrings.invite_friends_text, appName, permalink),
                    extraTitle = stringProvider.getString(CommonStrings.invite_friends_rich_title, appName),
                    noActivityFoundMessage = stringProvider.getString(AndroidUtilsR.string.error_no_compatible_app_found)
                )
            },
            onFailure = {
                Timber.e(it)
            }
        )
    }
}
