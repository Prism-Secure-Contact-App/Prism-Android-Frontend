#if (${PACKAGE_NAME} && ${PACKAGE_NAME} != "")package ${PACKAGE_NAME}#end

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import io.prism.android.compound.theme.PRISMTheme
import io.prism.android.libraries.designsystem.preview.PRISMPreview
import io.prism.android.libraries.designsystem.preview.PreviewsDayNight
import io.prism.android.libraries.designsystem.theme.components.Text

@Composable
fun ${NAME}View(
    state: ${NAME}State,
    modifier: Modifier = Modifier,
) {
    Box(modifier, contentAlignment = Alignment.Center) {
        Text(
            "${NAME} feature view",
            color = PRISMTheme.colors.textPrimary,
        )
    }
}

@PreviewsDayNight
@Composable
internal fun ${NAME}ViewPreview(
    @PreviewParameter(${NAME}StateProvider::class) state: ${NAME}State
) = PRISMPreview {
    ${NAME}View(
        state = state,
    )
}
