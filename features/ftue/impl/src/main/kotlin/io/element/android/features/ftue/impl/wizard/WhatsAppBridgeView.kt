package io.prism.android.features.ftue.impl.wizard

import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.prism.android.compound.tokens.generated.CompoundIcons
import io.prism.android.libraries.architecture.AsyncAction
import io.prism.android.libraries.designsystem.atomic.molecules.IconTitleSubtitleMolecule
import io.prism.android.libraries.designsystem.components.BigIcon
import io.prism.android.libraries.designsystem.components.button.BackButton
import io.prism.android.libraries.designsystem.theme.components.*
import io.prism.android.libraries.ui.strings.CommonStrings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WhatsAppBridgeView(
    state: BridgeState,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    BackButton(onClick = { state.eventSink(BridgeEvents.Back) })
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
        ) {
            IconTitleSubtitleMolecule(
                iconStyle = BigIcon.Style.Default(CompoundIcons.ShareAndroid()),
                title = "WhatsApp Bridge",
                subTitle = "Connect your WhatsApp account to PRISM. Send and receive messages from your WhatsApp contacts directly here. All chats are securely synced."
            )
            
            Spacer(Modifier.height(32.dp))
            
            if (state.connectAction is AsyncAction.Loading) {
                CircularProgressIndicator()
                Spacer(Modifier.height(16.dp))
                Text("Connecting to WhatsApp...")
            } else {
                Spacer(Modifier.weight(1f))
                Button(
                    text = "Connect WhatsApp",
                    onClick = { state.eventSink(BridgeEvents.Connect) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))
                OutlinedButton(
                    text = stringResource(CommonStrings.action_skip),
                    onClick = { state.eventSink(BridgeEvents.Skip) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}
