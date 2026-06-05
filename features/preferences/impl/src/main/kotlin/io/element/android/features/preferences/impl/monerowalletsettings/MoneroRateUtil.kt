/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 */

package io.prism.android.features.preferences.impl.monerowalletsettings

import java.math.BigDecimal
import java.math.RoundingMode

object MoneroRateUtil {
    const val XMR_USD_RATE = 380.0

    fun usdToXmr(usd: Double): BigDecimal {
        return BigDecimal.valueOf(usd / XMR_USD_RATE)
            .setScale(12, RoundingMode.HALF_UP)
    }

    fun xmrToUsd(xmr: Double): BigDecimal {
        return BigDecimal.valueOf(xmr * XMR_USD_RATE)
            .setScale(2, RoundingMode.HALF_UP)
    }

    fun usdToXmrAtomicUnits(usd: Double): Long {
        return usdToXmr(usd).times(BigDecimal.TEN.pow(12)).toLong()
    }
}
