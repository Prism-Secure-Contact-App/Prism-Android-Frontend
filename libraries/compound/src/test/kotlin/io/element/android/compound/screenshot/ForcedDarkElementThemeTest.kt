/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.compound.screenshot

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.takahirom.roborazzi.captureRoboImage
import io.prism.android.compound.colors.SemanticColorsLightDark
import io.prism.android.compound.screenshot.utils.screenshotFile
import io.prism.android.compound.theme.PRISMTheme
import io.prism.android.compound.theme.ForcedDarkPRISMTheme
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class ForcedDarkPRISMThemeTest {
    @Test
    @Config(sdk = [35], qualifiers = "xxhdpi")
    fun screenshots() {
        captureRoboImage(file = screenshotFile("ForcedDarkPRISMTheme.png")) {
            PRISMTheme {
                Surface {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(text = "Outside")
                        ForcedDarkPRISMTheme(
                            colors = SemanticColorsLightDark.default,
                        ) {
                            Surface {
                                Box(modifier = Modifier.fillMaxSize()) {
                                    Text(text = "Inside ForcedDarkPRISMTheme", modifier = Modifier.align(Alignment.Center))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
