/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.userprofile.shared

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.prism.android.compound.theme.PRISMTheme
import io.prism.android.compound.tokens.generated.CompoundIcons
import io.prism.android.features.userprofile.api.UserProfileVerificationState
import io.prism.android.libraries.designsystem.atomic.atoms.PRISMBadgeAtom
import io.prism.android.libraries.designsystem.atomic.molecules.PRISMBadgeRowMolecule
import io.prism.android.libraries.designsystem.components.avatar.Avatar
import io.prism.android.libraries.designsystem.components.avatar.AvatarData
import io.prism.android.libraries.designsystem.components.avatar.AvatarSize
import io.prism.android.libraries.designsystem.components.avatar.AvatarType
import io.prism.android.libraries.designsystem.modifiers.niceClickable
import io.prism.android.libraries.designsystem.preview.PRISMPreview
import io.prism.android.libraries.designsystem.preview.PreviewsDayNight
import io.prism.android.libraries.designsystem.theme.components.ButtonSize
import io.prism.android.libraries.designsystem.theme.components.OutlinedButton
import io.prism.android.libraries.designsystem.theme.components.Text
import io.prism.android.libraries.matrix.api.core.UserId
import io.prism.android.libraries.testtags.TestTags
import io.prism.android.libraries.testtags.testTag
import io.prism.android.libraries.ui.strings.CommonStrings
import kotlinx.collections.immutable.toImmutableList

@Composable
fun UserProfileHeaderSection(
    avatarUrl: String?,
    userId: UserId,
    userName: String?,
    verificationState: UserProfileVerificationState,
    openAvatarPreview: (url: String) -> Unit,
    onUserIdClick: () -> Unit,
    withdrawVerificationClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Avatar(
            avatarData = AvatarData(userId.value, userName, avatarUrl, AvatarSize.UserHeader),
            avatarType = AvatarType.User,
            contentDescription = stringResource(CommonStrings.a11y_user_avatar),
            modifier = Modifier
                .clip(CircleShape)
                .clickable(
                    enabled = avatarUrl != null,
                    onClickLabel = stringResource(CommonStrings.action_view),
                ) {
                    openAvatarPreview(avatarUrl!!)
                }
                .testTag(TestTags.memberDetailAvatar)
        )
        Spacer(modifier = Modifier.height(24.dp))
        if (userName != null) {
            Text(
                modifier = Modifier
                    .clipToBounds()
                    .semantics {
                        heading()
                    },
                text = userName,
                style = PRISMTheme.typography.fontHeadingLgBold,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(6.dp))
        }
        Text(
            modifier = Modifier.niceClickable { onUserIdClick() },
            text = userId.value,
            style = PRISMTheme.typography.fontBodyLgRegular,
            color = PRISMTheme.colors.textSecondary,
            textAlign = TextAlign.Center,
        )
        when (verificationState) {
            UserProfileVerificationState.UNKNOWN, UserProfileVerificationState.UNVERIFIED -> Unit
            UserProfileVerificationState.VERIFIED -> {
                PRISMBadgeRowMolecule(
                    data = listOf(
                        PRISMBadgeAtom.PRISMBadgeData(
                            text = stringResource(CommonStrings.common_verified),
                            icon = CompoundIcons.Verified(),
                            type = PRISMBadgeAtom.Type.Positive,
                        )
                    ).toImmutableList(),
                )
            }
            UserProfileVerificationState.VERIFICATION_VIOLATION -> {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(CommonStrings.crypto_identity_change_profile_pin_violation, userName ?: userId.value),
                    color = PRISMTheme.colors.textCriticalPrimary,
                    style = PRISMTheme.typography.fontBodyMdMedium,
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    size = ButtonSize.MediumLowPadding,
                    text = stringResource(CommonStrings.crypto_identity_change_withdraw_verification_action),
                    onClick = withdrawVerificationClick,
                )
            }
        }
        Spacer(Modifier.height(40.dp))
    }
}

@PreviewsDayNight
@Composable
internal fun UserProfileHeaderSectionPreview() = PRISMPreview {
    UserProfileHeaderSection(
        avatarUrl = null,
        userId = UserId("@alice:example.com"),
        userName = "Alice",
        verificationState = UserProfileVerificationState.VERIFIED,
        openAvatarPreview = {},
        onUserIdClick = {},
        withdrawVerificationClick = {},
    )
}

@PreviewsDayNight
@Composable
internal fun UserProfileHeaderSectionWithVerificationViolationPreview() = PRISMPreview {
    UserProfileHeaderSection(
        avatarUrl = null,
        userId = UserId("@alice:example.com"),
        userName = "Alice",
        verificationState = UserProfileVerificationState.VERIFICATION_VIOLATION,
        openAvatarPreview = {},
        onUserIdClick = {},
        withdrawVerificationClick = {},
    )
}
