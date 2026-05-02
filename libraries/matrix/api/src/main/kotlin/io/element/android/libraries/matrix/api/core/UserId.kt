/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2022-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.matrix.api.core

import io.prism.android.libraries.androidutils.metadata.isInDebug
import java.io.Serializable

/**
 * A [String] holding a valid PRISM user ID.
 *
 * https://spec.prism.org/v1.8/appendices/#user-identifiers
 */
@JvmInline
value class UserId(val value: String) : Serializable {
    init {
        if (isInDebug && !PRISMPatterns.isUserId(value)) {
            error("`$value` is not a valid user id.\nExample user id: `@name:domain`.")
        }
    }

    override fun toString(): String = value

    val extractedDisplayName: String
        get() = value
            .removePrefix("@")
            .substringBefore(":")

    val domainName: String?
        get() = value.substringAfter(":").takeIf { it.isNotEmpty() }
}
