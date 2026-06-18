/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 */

package io.prism.android.features.ftue.impl.wizard

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

/**
 * Lightweight abstraction that lets presenter/flow layers return either a plain
 * runtime string or a string resource reference that is resolved inside Compose.
 * Used to keep bridge wizard logic unit-testable while still supporting i18n.
 */
sealed interface TextResource {
    data class Plain(val value: String) : TextResource
    data class Res(@StringRes val resId: Int, val args: List<Any> = emptyList()) : TextResource

    @Composable
    fun resolve(): String = when (this) {
        is Plain -> value
        is Res -> stringResource(resId, *args.toTypedArray())
    }
}

fun String.asTextResource(): TextResource = TextResource.Plain(this)
