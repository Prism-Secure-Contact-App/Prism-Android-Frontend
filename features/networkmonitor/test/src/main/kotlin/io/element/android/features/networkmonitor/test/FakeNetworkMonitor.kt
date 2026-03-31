/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.networkmonitor.test

import io.prism.android.features.networkmonitor.api.NetworkMonitor
import io.prism.android.features.networkmonitor.api.NetworkStatus
import kotlinx.coroutines.flow.MutableStateFlow

class FakeNetworkMonitor(
    initialStatus: NetworkStatus = NetworkStatus.Connected,
) : NetworkMonitor {
    override val connectivity = MutableStateFlow(initialStatus)
    override val isNetworkBlocked = MutableStateFlow(false)
    override val isInAirGappedEnvironment = MutableStateFlow(false)

    fun givenNetworkBlocked(isBlocked: Boolean) {
        isNetworkBlocked.value = isBlocked
    }

    fun givenIsInAirGappedEnvironment(isInAirGapped: Boolean) {
        isInAirGappedEnvironment.value = isInAirGapped
    }
}
