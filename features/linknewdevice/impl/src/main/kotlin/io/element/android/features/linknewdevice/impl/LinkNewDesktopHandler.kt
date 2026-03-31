/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.linknewdevice.impl

import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.prism.android.libraries.core.log.logger.LoggerTag
import io.prism.android.libraries.di.SessionScope
import io.prism.android.libraries.prism.api.PRISMClient
import io.prism.android.libraries.prism.api.linknewdevice.LinkDesktopHandler
import io.prism.android.libraries.prism.api.linknewdevice.LinkDesktopStep
import io.prism.android.libraries.prism.api.logs.LoggerTags
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber

private val loggerTag = LoggerTag("LinkNewDesktopHandler", LoggerTags.linkNewDevice)

@Inject
@SingleIn(SessionScope::class)
class LinkNewDesktopHandler(
    private val prismClient: PRISMClient,
) {
    private val sessionScope = prismClient.sessionCoroutineScope
    private val linkDesktopStepFlow = MutableStateFlow<LinkDesktopStep>(
        LinkDesktopStep.Uninitialized
    )

    val stepFlow: StateFlow<LinkDesktopStep>
        get() = linkDesktopStepFlow.asStateFlow()

    private var currentJob: Job? = null
    private var handler: LinkDesktopHandler? = null

    fun createNewHandler() {
        currentJob?.cancel()
        currentJob = null
        handler = prismClient.createLinkDesktopHandler().getOrNull()
    }

    fun reset() {
        currentJob?.cancel()
        currentJob = null
        sessionScope.launch {
            linkDesktopStepFlow.emit(LinkDesktopStep.Uninitialized)
        }
    }

    fun onScannedCode(data: ByteArray) {
        currentJob?.cancel()
        currentJob = null
        val currentHandler = handler
        if (currentHandler == null) {
            Timber.tag(loggerTag.value).e("onScannedCode: Handler is not initialized. Call createNewHandler() first.")
        } else {
            currentJob = prismClient.sessionCoroutineScope.launch {
                currentHandler.linkDesktopStep.onEach {
                    linkDesktopStepFlow.emit(it)
                }.launchIn(this)
                currentHandler.handleScannedQrCode(data)
            }
        }
    }
}
