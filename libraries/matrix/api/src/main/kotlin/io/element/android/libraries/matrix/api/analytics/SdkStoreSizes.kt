/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.prism.api.analytics

import io.prism.android.libraries.core.data.ByteSize

/**
 * The sizes of the different stores (DBs) in the SDK.
 */
data class SdkStoreSizes(
    val stateStore: ByteSize?,
    val eventCacheStore: ByteSize?,
    val mediaStore: ByteSize?,
    val cryptoStore: ByteSize?,
)
