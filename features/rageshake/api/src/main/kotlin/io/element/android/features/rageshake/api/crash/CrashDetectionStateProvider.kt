/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2022-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.rageshake.api.crash

fun aCrashDetectionState() = CrashDetectionState(
    appName = "PRISM",
    crashDetected = false,
    eventSink = {}
)
