package io.prism.android.features.ftue.impl.wizard

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import io.prism.android.features.ftue.impl.state.DefaultFtueService

class WhatsAppBridgeNode(
    buildContext: BuildContext,
    private val ftueService: DefaultFtueService,
    private val onBack: () -> Unit,
) : Node(buildContext) {

    private val presenter = BridgePresenter(
        bridgeName = "WhatsApp",
        onConnected = { ftueService.completeCurrentStepAndAdvance() },
        onSkip = { ftueService.completeCurrentStepAndAdvance() },
        onBack = onBack
    )

    @Composable
    override fun View(modifier: Modifier) {
        val state = presenter.present()
        WhatsAppBridgeView(
            state = state,
            modifier = modifier
        )
    }
}
