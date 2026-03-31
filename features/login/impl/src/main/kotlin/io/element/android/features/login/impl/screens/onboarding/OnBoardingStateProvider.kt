/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.login.impl.screens.onboarding

import androidx.annotation.DrawableRes
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.prism.android.features.login.impl.login.LoginMode
import io.prism.android.features.login.impl.screens.onboarding.classic.LoginWithClassicState
import io.prism.android.features.login.impl.screens.onboarding.classic.aLoginWithClassicState
import io.prism.android.libraries.architecture.AsyncData
import io.prism.android.libraries.designsystem.R

open class OnBoardingStateProvider : PreviewParameterProvider<OnBoardingState> {
    override val values: Sequence<OnBoardingState>
        get() = sequenceOf(
            anOnBoardingState(),
            anOnBoardingState(canLoginWithQrCode = true),
            anOnBoardingState(canCreateAccount = true),
            anOnBoardingState(canLoginWithQrCode = true, canCreateAccount = true),
            anOnBoardingState(canLoginWithQrCode = true, canCreateAccount = true, canReportBug = true),
            anOnBoardingState(defaultAccountProvider = "prism.io", canCreateAccount = false, canReportBug = true),
            anOnBoardingState(customLogoResId = R.drawable.sample_background),
            anOnBoardingState(
                isAddingAccount = true,
                canLoginWithQrCode = true,
                canCreateAccount = true,
            ),
        )
}

fun anOnBoardingState(
    isAddingAccount: Boolean = false,
    productionApplicationName: String = "PRISM",
    defaultAccountProvider: String? = null,
    mustChooseAccountProvider: Boolean = false,
    canLoginWithQrCode: Boolean = false,
    canCreateAccount: Boolean = false,
    canReportBug: Boolean = false,
    version: String = "1.0.0",
    @DrawableRes
    customLogoResId: Int? = null,
    loginMode: AsyncData<LoginMode> = AsyncData.Uninitialized,
    loginWithClassicState: LoginWithClassicState = aLoginWithClassicState(),
    eventSink: (OnBoardingEvents) -> Unit = {},
) = OnBoardingState(
    isAddingAccount = isAddingAccount,
    productionApplicationName = productionApplicationName,
    defaultAccountProvider = defaultAccountProvider,
    mustChooseAccountProvider = mustChooseAccountProvider,
    canLoginWithQrCode = canLoginWithQrCode,
    canCreateAccount = canCreateAccount,
    canReportBug = canReportBug,
    version = version,
    loginMode = loginMode,
    onBoardingLogoResId = customLogoResId,
    loginWithClassicState = loginWithClassicState,
    eventSink = eventSink,
)
