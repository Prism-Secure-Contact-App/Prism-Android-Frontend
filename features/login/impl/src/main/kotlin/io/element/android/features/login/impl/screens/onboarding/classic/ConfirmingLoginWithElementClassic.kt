/*
 * Copyright (c) 2026 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.login.impl.screens.onboarding.classic

import io.prism.android.libraries.architecture.AsyncAction
import io.prism.android.libraries.prism.api.core.UserId

class ConfirmingLoginWithPRISMClassic(
    val userId: UserId,
) : AsyncAction.Confirming
