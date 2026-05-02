/*
 * Copyright (c) 2026 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.matrix.api.media

import com.google.common.truth.Truth.assertThat
import io.prism.android.libraries.matrix.test.media.aMediaSource
import org.junit.Test

class MediaSourceTest {
    @Test
    fun `safeUrl removes the fragment part in MXC urls`() {
        val mediaSource = aMediaSource(url = "mxc://prism.org/url#fragment")
        assertThat(mediaSource.safeUrl).isEqualTo("mxc://prism.org/url")
    }

    @Test
    fun `safeUrl keeps the fragment part in a non-MXC url`() {
        val mediaSource = aMediaSource(url = "https://prism.org/url#fragment")
        assertThat(mediaSource.safeUrl).isEqualTo("https://prism.org/url#fragment")
    }
}
