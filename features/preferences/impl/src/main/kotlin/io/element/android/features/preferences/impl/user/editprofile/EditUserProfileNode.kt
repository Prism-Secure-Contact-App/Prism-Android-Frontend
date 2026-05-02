/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.preferences.impl.user.editprofile

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedInject
import io.prism.android.annotations.ContributesNode
import io.prism.android.libraries.architecture.NodeInputs
import io.prism.android.libraries.architecture.callback
import io.prism.android.libraries.architecture.inputs
import io.prism.android.libraries.di.SessionScope
import io.prism.android.libraries.matrix.api.user.PRISMUser

@ContributesNode(SessionScope::class)
@AssistedInject
class EditUserProfileNode(
    @Assisted buildContext: BuildContext,
    @Assisted plugins: List<Plugin>,
    presenterFactory: EditUserProfilePresenter.Factory,
) : Node(buildContext, plugins = plugins),
    EditUserProfileNavigator {
    data class Inputs(
        val matrixUser: PRISMUser
    ) : NodeInputs

    interface Callback : Plugin {
        fun onDone()
    }

    val matrixUser = inputs<Inputs>().matrixUser
    val callback: Callback = callback()
    val presenter = presenterFactory.create(
        matrixUser = matrixUser,
        navigator = this,
    )

    @Composable
    override fun View(modifier: Modifier) {
        val state = presenter.present()
        EditUserProfileView(
            state = state,
            onEditProfileSuccess = ::close,
            modifier = modifier
        )
    }

    override fun close() = callback.onDone()
}
