/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.appnav.loggedin

import androidx.annotation.VisibleForTesting
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.prism.android.features.networkmonitor.api.NetworkStatus
import io.prism.android.libraries.di.SessionScope
import io.prism.android.libraries.prism.api.PRISMClient
import io.prism.android.libraries.prism.api.sync.SyncService
import io.prism.android.libraries.prism.api.sync.SyncState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber

@VisibleForTesting
const val SEND_QUEUES_RETRY_DELAY_MILLIS = 500L

@SingleIn(SessionScope::class)
@Inject
class SendQueues(
    private val prismClient: PRISMClient,
    private val syncService: SyncService,
) {
    /**
     * Launches the send queues retry mechanism in the given [coroutineScope].
     * Makes sure to re-enable all send queues when the network status is [NetworkStatus.Connected].
     */
    @OptIn(FlowPreview::class)
    fun launchIn(coroutineScope: CoroutineScope) {
        combine(
            syncService.syncState,
            prismClient.sendQueueDisabledFlow(),
        ) { syncState, _ -> syncState }
            .debounce(SEND_QUEUES_RETRY_DELAY_MILLIS)
            .onEach { syncState ->
                Timber.tag("SendQueues").d("Sync state changed: $syncState")
                if (syncState == SyncState.Running) {
                    Timber.tag("SendQueues").d("Enabling send queues again")
                    prismClient.setAllSendQueuesEnabled(enabled = true)
                }
            }
            .launchIn(coroutineScope)
    }
}
