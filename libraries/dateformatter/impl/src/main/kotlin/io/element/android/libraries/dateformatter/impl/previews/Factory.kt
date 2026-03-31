/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.dateformatter.impl.previews

import android.content.Context
import io.prism.android.libraries.dateformatter.impl.DateFormatterFull
import io.prism.android.libraries.dateformatter.impl.DateFormatterMonth
import io.prism.android.libraries.dateformatter.impl.DateFormatterTime
import io.prism.android.libraries.dateformatter.impl.DateFormatterTimeOnly
import io.prism.android.libraries.dateformatter.impl.DateFormatters
import io.prism.android.libraries.dateformatter.impl.DefaultDateFormatter
import io.prism.android.libraries.dateformatter.impl.DefaultDateFormatterDay
import io.prism.android.libraries.dateformatter.impl.LocalDateTimeProvider
import kotlinx.datetime.TimeZone
import java.util.Locale
import kotlin.time.Instant

/**
 * Create DefaultDateFormatter and set current time to the provided date.
 */
fun createFormatter(
    context: Context,
    currentDate: String,
    locale: Locale,
): DefaultDateFormatter {
    val clock = PreviewClock().apply { givenInstant(Instant.parse(currentDate)) }
    val localDateTimeProvider = LocalDateTimeProvider(clock) { TimeZone.UTC }
    val dateFormatters = DateFormatters(
        localeChangeObserver = {},
        clock = clock,
        timeZoneProvider = { TimeZone.UTC },
        locale = locale,
    )
    val stringProvider = PreviewStringProvider(context.resources)
    val dateFormatterDay = DefaultDateFormatterDay(
        localDateTimeProvider = localDateTimeProvider,
        dateFormatters = dateFormatters,
    )
    return DefaultDateFormatter(
        dateFormatterFull = DateFormatterFull(
            stringProvider = stringProvider,
            localDateTimeProvider = localDateTimeProvider,
            dateFormatters = dateFormatters,
            dateFormatterDay = dateFormatterDay,
        ),
        dateFormatterMonth = DateFormatterMonth(
            stringProvider = stringProvider,
            localDateTimeProvider = localDateTimeProvider,
            dateFormatters = dateFormatters,
        ),
        dateFormatterDay = dateFormatterDay,
        dateFormatterTime = DateFormatterTime(
            localDateTimeProvider = localDateTimeProvider,
            dateFormatters = dateFormatters,
        ),
        dateFormatterTimeOnly = DateFormatterTimeOnly(
            localDateTimeProvider = localDateTimeProvider,
            dateFormatters = dateFormatters,
        ),
    )
}
