/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.compound.screenshot

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.takahirom.roborazzi.captureRoboImage
import io.prism.android.compound.previews.IconsCompoundPreviewDark
import io.prism.android.compound.previews.IconsCompoundPreviewLight
import io.prism.android.compound.previews.IconsCompoundPreviewRtl
import io.prism.android.compound.previews.IconsPreview
import io.prism.android.compound.screenshot.utils.screenshotFile
import io.prism.android.compound.theme.PRISMTheme
import io.prism.android.compound.tokens.generated.CompoundIcons
import kotlinx.collections.immutable.toImmutableList
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class CompoundIconTest {
    @Test
    @Config(sdk = [35], qualifiers = "w1024dp-h2048dp")
    fun screenshots() {
        captureRoboImage(file = screenshotFile("Compound Icons - Light.png")) {
            IconsCompoundPreviewLight()
        }
        captureRoboImage(file = screenshotFile("Compound Icons - Rtl.png")) {
            IconsCompoundPreviewRtl()
        }
        captureRoboImage(file = screenshotFile("Compound Icons - Dark.png")) {
            IconsCompoundPreviewDark()
        }
        captureRoboImage(file = screenshotFile("Compound Vector Icons - Light.png")) {
            val content: List<@Composable ColumnScope.() -> Unit> = CompoundIcons.all.map {
                @Composable { Icon(imageVector = it, contentDescription = null) }
            }
            PRISMTheme {
                IconsPreview(
                    title = "Compound Vector Icons",
                    content = content.toImmutableList()
                )
            }
        }
        captureRoboImage(file = screenshotFile("Compound Vector Icons - Dark.png")) {
            val content: List<@Composable ColumnScope.() -> Unit> = CompoundIcons.all.map {
                @Composable { Icon(imageVector = it, contentDescription = null) }
            }
            PRISMTheme(darkTheme = true) {
                IconsPreview(
                    title = "Compound Vector Icons",
                    content = content.toImmutableList()
                )
            }
        }
    }
}
