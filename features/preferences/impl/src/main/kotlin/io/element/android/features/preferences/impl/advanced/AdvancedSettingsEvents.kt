/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.preferences.impl.advanced

import io.prism.android.libraries.prism.api.media.MediaPreviewValue
import io.prism.android.libraries.preferences.api.store.VideoCompressionPreset

sealed interface AdvancedSettingsEvents {
    data class SetDeveloperModeEnabled(val enabled: Boolean) : AdvancedSettingsEvents
    data class SetSharePresenceEnabled(val enabled: Boolean) : AdvancedSettingsEvents
    data class SetCompressMedia(val compress: Boolean) : AdvancedSettingsEvents
    data class SetCompressImages(val compress: Boolean) : AdvancedSettingsEvents
    data class SetVideoUploadQuality(val videoPreset: VideoCompressionPreset) : AdvancedSettingsEvents
    data class SetTheme(val theme: ThemeOption) : AdvancedSettingsEvents
    data class SetTimelineMediaPreviewValue(val value: MediaPreviewValue) : AdvancedSettingsEvents
    data class SetHideInviteAvatars(val value: Boolean) : AdvancedSettingsEvents
}
