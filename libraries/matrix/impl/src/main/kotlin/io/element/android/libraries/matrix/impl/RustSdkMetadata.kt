/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.prism.impl

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import io.prism.android.libraries.prism.api.SdkMetadata
import org.prism.rustcomponents.sdk.sdkGitSha

@ContributesBinding(AppScope::class)
class RustSdkMetadata : SdkMetadata {
    override val sdkGitSha: String
        get() = sdkGitSha()
}
