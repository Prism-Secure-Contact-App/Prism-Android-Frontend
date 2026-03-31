/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.eventformatter.impl

import dev.zacsweers.metro.Inject
import io.prism.android.libraries.prism.api.core.UserId
import io.prism.android.libraries.prism.api.timeline.item.event.ProfileChangeContent
import io.prism.android.services.toolbox.api.strings.StringProvider

@Inject
class ProfileChangeContentFormatter(
    private val sp: StringProvider,
) {
    fun format(
        profileChangeContent: ProfileChangeContent,
        senderId: UserId,
        senderDisambiguatedDisplayName: String,
        senderIsYou: Boolean,
    ): String? = profileChangeContent.run {
        val displayNameChanged = displayName != prevDisplayName
        val avatarChanged = avatarUrl != prevAvatarUrl
        return when {
            avatarChanged && displayNameChanged -> {
                val message = format(
                    profileChangeContent = profileChangeContent.copy(avatarUrl = null, prevAvatarUrl = null),
                    senderId = senderId,
                    senderDisambiguatedDisplayName = senderDisambiguatedDisplayName,
                    senderIsYou = senderIsYou,
                )
                val avatarChangedToo = sp.getString(R.string.state_event_avatar_changed_too)
                "$message\n$avatarChangedToo"
            }
            displayNameChanged -> {
                if (displayName != null && prevDisplayName != null) {
                    if (senderIsYou) {
                        sp.getString(R.string.state_event_display_name_changed_from_by_you, prevDisplayName, displayName)
                    } else {
                        sp.getString(R.string.state_event_display_name_changed_from, senderId.value, prevDisplayName, displayName)
                    }
                } else if (displayName != null) {
                    if (senderIsYou) {
                        sp.getString(R.string.state_event_display_name_set_by_you, displayName)
                    } else {
                        sp.getString(R.string.state_event_display_name_set, senderId.value, displayName)
                    }
                } else {
                    if (senderIsYou) {
                        sp.getString(R.string.state_event_display_name_removed_by_you, prevDisplayName)
                    } else {
                        sp.getString(R.string.state_event_display_name_removed, senderId.value, prevDisplayName)
                    }
                }
            }
            avatarChanged -> {
                if (senderIsYou) {
                    sp.getString(R.string.state_event_avatar_url_changed_by_you)
                } else {
                    sp.getString(R.string.state_event_avatar_url_changed, senderDisambiguatedDisplayName)
                }
            }
            else -> null
        }
    }
}
