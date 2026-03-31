/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.prism.impl.timeline.item.event

import io.prism.android.libraries.prism.api.core.EventId
import io.prism.android.libraries.prism.api.core.TransactionId
import io.prism.android.libraries.prism.api.timeline.item.event.EventOrTransactionId
import org.prism.rustcomponents.sdk.EventOrTransactionId as RustEventOrTransactionId

fun RustEventOrTransactionId.map(): EventOrTransactionId = when (this) {
    is RustEventOrTransactionId.EventId -> EventOrTransactionId.Event(EventId(eventId))
    is RustEventOrTransactionId.TransactionId -> EventOrTransactionId.Transaction(TransactionId(transactionId))
}
