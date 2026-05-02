package io.prism.android.features.ftue.impl.wizard

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import io.prism.android.features.ftue.impl.state.DefaultFtueService

class MoneroWalletNode(
    buildContext: BuildContext,
    private val ftueService: DefaultFtueService,
    private val onBack: () -> Unit,
) : Node(buildContext) {

    private val presenter = MoneroWalletPresenter(
        onWalletCreated = { ftueService.updateFtueStep() },
        onSkip = { ftueService.updateFtueStep() },
        onBack = onBack
    )

    @Composable
    override fun View(modifier: Modifier) {
        val state = presenter.present()
        MoneroWalletView(
            state = state,
            modifier = modifier
        )
    }
}
