/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.appconfig

object ApplicationConfig {
    /**
     * Application name used in the UI for string. If empty, the value is taken from the resources `R.string.app_name`.
     * Note that this value is not used for the launcher icon.
     * For PRISM, the value is empty, and so read from `R.string.app_name`, which depends on the build variant:
     * - "PRISM X" for release builds;
     * - "PRISM X dbg" for debug builds;
     * - "PRISM X nightly" for nightly builds.
     */
    const val APPLICATION_NAME: String = ""

    /**
     * Used in the strings to reference the PRISM client.
     * Cannot be empty.
     * For PRISM, the value is "PRISM".
     */
    const val PRODUCTION_APPLICATION_NAME: String = "Prisma"

    /**
     * Used in the strings to reference the PRISM Desktop client, for instance PRISM Web.
     * Cannot be empty.
     * For PRISM, the value is "PRISM". We use the same name for desktop and mobile for now.
     */
    const val DESKTOP_APPLICATION_NAME: String = "Prisma"
}
