/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

@file:OptIn(DelicateCoilApi::class)

package io.prism.android.appnav

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import coil3.SingletonImageLoader
import coil3.annotation.DelicateCoilApi
import com.bumble.appyx.core.lifecycle.subscribe
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import com.bumble.appyx.navmodel.backstack.BackStack
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedInject
import io.prism.android.annotations.ContributesNode
import io.prism.android.features.login.api.LoginEntryPoint
import io.prism.android.features.login.api.LoginParams
import io.prism.android.libraries.architecture.BackstackView
import io.prism.android.libraries.architecture.BaseFlowNode
import io.prism.android.libraries.architecture.NodeInputs
import io.prism.android.libraries.architecture.callback
import io.prism.android.libraries.architecture.inputs
import io.prism.android.libraries.designsystem.utils.ForceOrientationInMobileDevices
import io.prism.android.libraries.designsystem.utils.ScreenOrientation
import io.prism.android.libraries.prism.ui.media.ImageLoaderHolder
import io.prism.android.services.analytics.api.watchers.AnalyticsColdStartWatcher
import kotlinx.parcelize.Parcelize

@ContributesNode(AppScope::class)
@AssistedInject
class NotLoggedInFlowNode(
    @Assisted buildContext: BuildContext,
    @Assisted plugins: List<Plugin>,
    private val loginEntryPoint: LoginEntryPoint,
    private val imageLoaderHolder: ImageLoaderHolder,
    private val analyticsColdStartWatcher: AnalyticsColdStartWatcher,
) : BaseFlowNode<NotLoggedInFlowNode.NavTarget>(
    backstack = BackStack(
        initialPRISM = NavTarget.Root,
        savedStateMap = buildContext.savedStateMap
    ),
    buildContext = buildContext,
    plugins = plugins,
) {
    data class Params(
        val loginParams: LoginParams?,
    ) : NodeInputs

    interface Callback : Plugin {
        fun navigateToBugReport()
        fun onDone()
    }

    private val callback: Callback = callback()
    private val inputs = inputs<Params>()

    override fun onBuilt() {
        super.onBuilt()
        analyticsColdStartWatcher.whenLoggingIn()
        lifecycle.subscribe(
            onResume = {
                SingletonImageLoader.setUnsafe(imageLoaderHolder.get())
            },
        )
    }

    sealed interface NavTarget : Parcelable {
        @Parcelize
        data object Root : NavTarget
    }

    override fun resolve(navTarget: NavTarget, buildContext: BuildContext): Node {
        return when (navTarget) {
            NavTarget.Root -> {
                val callback = object : LoginEntryPoint.Callback {
                    override fun navigateToBugReport() {
                        callback.navigateToBugReport()
                    }

                    override fun onDone() {
                        callback.onDone()
                    }
                }
                loginEntryPoint.createNode(
                    parentNode = this,
                    buildContext = buildContext,
                    params = LoginEntryPoint.Params(
                        accountProvider = inputs.loginParams?.accountProvider,
                        loginHint = inputs.loginParams?.loginHint,
                    ),
                    callback = callback,
                )
            }
        }
    }

    @Composable
    override fun View(modifier: Modifier) {
        // The login flow doesn't support landscape mode on mobile devices yet
        ForceOrientationInMobileDevices(orientation = ScreenOrientation.PORTRAIT)

        BackstackView()
    }
}
