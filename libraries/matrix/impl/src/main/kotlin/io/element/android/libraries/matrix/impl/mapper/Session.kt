/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.prism.impl.mapper

import io.prism.android.libraries.prism.api.auth.external.ExternalSession
import io.prism.android.libraries.prism.impl.paths.SessionPaths
import io.prism.android.libraries.sessionstorage.api.LoginType
import io.prism.android.libraries.sessionstorage.api.SessionData
import org.prism.rustcomponents.sdk.Session
import java.util.Date

internal fun Session.toSessionData(
    isTokenValid: Boolean,
    loginType: LoginType,
    passphrase: String?,
    sessionPaths: SessionPaths,
    homeserverUrl: String? = null,
) = SessionData(
    userId = userId,
    deviceId = deviceId,
    accessToken = accessToken,
    refreshToken = refreshToken,
    homeserverUrl = homeserverUrl ?: this.homeserverUrl,
    oidcData = oidcData,
    loginTimestamp = Date(),
    isTokenValid = isTokenValid,
    loginType = loginType,
    passphrase = passphrase,
    sessionPath = sessionPaths.fileDirectory.absolutePath,
    cachePath = sessionPaths.cacheDirectory.absolutePath,
    // Note: position and lastUsageIndex will be set by the SessionStore when adding the session
    position = 0,
    lastUsageIndex = 0,
    userDisplayName = null,
    userAvatarUrl = null,
)

internal fun ExternalSession.toSessionData(
    isTokenValid: Boolean,
    loginType: LoginType,
    passphrase: String?,
    sessionPaths: SessionPaths,
) = SessionData(
    userId = userId,
    deviceId = deviceId,
    accessToken = accessToken,
    refreshToken = refreshToken,
    homeserverUrl = homeserverUrl,
    oidcData = null,
    loginTimestamp = Date(),
    isTokenValid = isTokenValid,
    loginType = loginType,
    passphrase = passphrase,
    sessionPath = sessionPaths.fileDirectory.absolutePath,
    cachePath = sessionPaths.cacheDirectory.absolutePath,
    position = 0,
    lastUsageIndex = 0,
    userDisplayName = null,
    userAvatarUrl = null,
)
