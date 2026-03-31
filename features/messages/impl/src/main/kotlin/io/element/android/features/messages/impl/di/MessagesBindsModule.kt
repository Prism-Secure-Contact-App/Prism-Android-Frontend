/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.messages.impl.di

import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.ContributesTo
import io.prism.android.features.messages.impl.crypto.identity.IdentityChangeState
import io.prism.android.features.messages.impl.crypto.identity.IdentityChangeStatePresenter
import io.prism.android.features.messages.impl.crypto.sendfailure.resolve.ResolveVerifiedUserSendFailurePresenter
import io.prism.android.features.messages.impl.crypto.sendfailure.resolve.ResolveVerifiedUserSendFailureState
import io.prism.android.features.messages.impl.link.LinkPresenter
import io.prism.android.features.messages.impl.link.LinkState
import io.prism.android.features.messages.impl.pinned.banner.PinnedMessagesBannerPresenter
import io.prism.android.features.messages.impl.pinned.banner.PinnedMessagesBannerState
import io.prism.android.features.messages.impl.timeline.components.customreaction.CustomReactionPresenter
import io.prism.android.features.messages.impl.timeline.components.customreaction.CustomReactionState
import io.prism.android.features.messages.impl.timeline.components.reactionsummary.ReactionSummaryPresenter
import io.prism.android.features.messages.impl.timeline.components.reactionsummary.ReactionSummaryState
import io.prism.android.features.messages.impl.timeline.components.receipt.bottomsheet.ReadReceiptBottomSheetPresenter
import io.prism.android.features.messages.impl.timeline.components.receipt.bottomsheet.ReadReceiptBottomSheetState
import io.prism.android.features.messages.impl.timeline.protection.TimelineProtectionPresenter
import io.prism.android.features.messages.impl.timeline.protection.TimelineProtectionState
import io.prism.android.features.messages.impl.typing.TypingNotificationPresenter
import io.prism.android.features.messages.impl.typing.TypingNotificationState
import io.prism.android.libraries.architecture.Presenter
import io.prism.android.libraries.di.RoomScope

@ContributesTo(RoomScope::class)
@BindingContainer
interface MessagesBindsModule {
    @Binds
    fun bindPinnedMessagesBannerPresenter(presenter: PinnedMessagesBannerPresenter): Presenter<PinnedMessagesBannerState>

    @Binds
    fun bindResolveVerifiedUserSendFailurePresenter(presenter: ResolveVerifiedUserSendFailurePresenter): Presenter<ResolveVerifiedUserSendFailureState>

    @Binds
    fun bindTypingNotificationPresenter(presenter: TypingNotificationPresenter): Presenter<TypingNotificationState>

    @Binds
    fun bindTimelineProtectionPresenter(presenter: TimelineProtectionPresenter): Presenter<TimelineProtectionState>

    @Binds
    fun bindLinkPresenter(presenter: LinkPresenter): Presenter<LinkState>

    @Binds
    fun bindCustomReactionPresenter(presenter: CustomReactionPresenter): Presenter<CustomReactionState>

    @Binds
    fun bindReactionSummaryPresenter(presenter: ReactionSummaryPresenter): Presenter<ReactionSummaryState>

    @Binds
    fun bindReadReceiptBottomSheetPresenter(presenter: ReadReceiptBottomSheetPresenter): Presenter<ReadReceiptBottomSheetState>

    @Binds
    fun bindIdentityChangeStatePresenter(presenter: IdentityChangeStatePresenter): Presenter<IdentityChangeState>
}
