/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.login.impl.screens.createaccount

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.prism.android.compound.theme.PRISMTheme
import io.prism.android.features.login.impl.R
import io.prism.android.libraries.designsystem.components.async.AsyncActionView
import io.prism.android.libraries.designsystem.components.button.BackButton
import io.prism.android.libraries.designsystem.preview.PRISMPreview
import io.prism.android.libraries.designsystem.preview.PreviewsDayNight
import io.prism.android.libraries.designsystem.theme.components.*
import io.prism.android.libraries.ui.strings.CommonStrings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAccountView(
    state: CreateAccountState,
    onBackClick: () -> Unit,
    onOpenExternalUrl: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                titleStr = stringResource(R.string.screen_create_account_title),
                navigationIcon = {
                    BackButton(onClick = onBackClick)
                },
            )
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .padding(contentPadding)
                .consumeWindowInsets(contentPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 24.dp)
        ) {
            Text(
                text = "Create your PRISM account",
                style = PRISMTheme.typography.fontHeadingMdBold,
                color = PRISMTheme.colors.textPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Choose a unique username and a strong password to get started.",
                style = PRISMTheme.typography.fontBodyMdRegular,
                color = PRISMTheme.colors.textSecondary
            )
            Spacer(modifier = Modifier.height(32.dp))

            TextField(
                value = state.username,
                onValueChange = { state.eventSink(CreateAccountEvents.SetUsername(it)) },
                label = "Username",
                placeholder = "e.g. alice",
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = state.password,
                onValueChange = { state.eventSink(CreateAccountEvents.SetPassword(it)) },
                label = stringResource(CommonStrings.common_password),
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = state.passwordConfirm,
                onValueChange = { state.eventSink(CreateAccountEvents.SetPasswordConfirm(it)) },
                label = "Confirm Password",
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                validity = if (state.password.isNotEmpty() && state.passwordConfirm.isNotEmpty() && state.password != state.passwordConfirm) TextFieldValidity.Invalid else TextFieldValidity.None,
            )
            
            if (state.password.isNotEmpty() && state.passwordConfirm.isNotEmpty() && state.password != state.passwordConfirm) {
                Text(
                    text = "Passwords do not match",
                    color = PRISMTheme.colors.textCriticalPrimary,
                    style = PRISMTheme.typography.fontBodySmRegular,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                text = stringResource(CommonStrings.action_continue),
                onClick = { state.eventSink(CreateAccountEvents.Submit) },
                modifier = Modifier.fillMaxWidth(),
                enabled = state.isSubmitEnabled
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "By creating an account, you agree to the Terms of Service and Privacy Policy.",
                style = PRISMTheme.typography.fontBodySmRegular,
                color = PRISMTheme.colors.textSecondary,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    AsyncActionView(
        async = state.createAction,
        onSuccess = {},
        onErrorDismiss = onBackClick,
        onRetry = null
    )
}

@PreviewsDayNight
@Composable
internal fun CreateAccountViewPreview(@PreviewParameter(CreateAccountStateProvider::class) state: CreateAccountState) = PRISMPreview {
    CreateAccountView(
        state = state,
        onBackClick = {},
        onOpenExternalUrl = {},
    )
}
