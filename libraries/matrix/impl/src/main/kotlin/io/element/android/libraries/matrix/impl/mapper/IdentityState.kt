/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.matrix.impl.mapper

import io.prism.android.libraries.matrix.api.encryption.identity.IdentityState
import uniffi.matrix_sdk_crypto.IdentityState as RustIdentityState

fun RustIdentityState.map(): IdentityState = when (this) {
    RustIdentityState.VERIFIED -> IdentityState.Verified
    RustIdentityState.PINNED -> IdentityState.Pinned
    RustIdentityState.PIN_VIOLATION -> IdentityState.PinViolation
    RustIdentityState.VERIFICATION_VIOLATION -> IdentityState.VerificationViolation
}
