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
import io.prism.android.libraries.prism.api.linknewdevice.LinkMobileHandler
import io.prism.android.libraries.prism.api.linknewdevice.LinkMobileStep
import io.prism.android.libraries.prism.api.logs.LoggerTags
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber

private val loggerTag = LoggerTag("LinkNewMobileHandler", LoggerTags.linkNewDevice)

@Inject
@SingleIn(SessionScope::class)
class LinkNewMobileHandler(
    private val prismClient: PRISMClient,
) {
    private val sessionScope = prismClient.sessionCoroutineScope
    private var currentJob: Job? = null
    private var handler: LinkMobileHandler? = null

    private val linkMobileStepFlow = MutableStateFlow<LinkMobileStep>(
        LinkMobileStep.Uninitialized
    )

    val stepFlow: StateFlow<LinkMobileStep>
        get() = linkMobileStepFlow.asStateFlow()

    fun createAndStartNewHandler() {
        Timber.tag(loggerTag.value).d("createAndStartNewHandler()")
        currentJob?.cancel()
        handler = prismClient.createLinkMobileHandler().getOrNull()
        handler?.let { h ->
            currentJob = sessionScope.launch {
                h.linkMobileStep
                    .onEach {
                        linkMobileStepFlow.emit(it)
                    }
                    .launchIn(this)
                h.start()
            }
        }
    }

    fun reset() {
        currentJob?.cancel()
        currentJob = null
        sessionScope.launch {
            linkMobileStepFlow.emit(LinkMobileStep.Uninitialized)
        }
    }
}
