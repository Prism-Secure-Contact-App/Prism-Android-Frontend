/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.licenses.impl.list

import io.prism.android.features.licenses.impl.model.DependencyLicenseItem
import io.prism.android.libraries.architecture.AsyncData
import kotlinx.collections.immutable.ImmutableList

data class DependencyLicensesListState(
    val licenses: AsyncData<ImmutableList<DependencyLicenseItem>>,
    val filter: String,
    val eventSink: (DependencyLicensesListEvent) -> Unit,
)
