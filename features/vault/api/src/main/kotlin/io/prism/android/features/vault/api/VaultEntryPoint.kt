/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 */

package io.prism.android.features.vault.api

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node

interface VaultEntryPoint {
    fun createNode(buildContext: BuildContext, onBack: () -> Unit): Node
}
