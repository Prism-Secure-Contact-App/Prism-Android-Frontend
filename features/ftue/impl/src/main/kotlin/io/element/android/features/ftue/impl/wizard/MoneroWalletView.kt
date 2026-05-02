package io.prism.android.features.ftue.impl.wizard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import io.prism.android.compound.theme.PRISMTheme
import io.prism.android.compound.tokens.generated.CompoundIcons
import io.prism.android.libraries.architecture.AsyncAction
import io.prism.android.libraries.designsystem.atomic.molecules.IconTitleSubtitleMolecule
import io.prism.android.libraries.designsystem.components.BigIcon
import io.prism.android.libraries.designsystem.components.button.BackButton
import io.prism.android.libraries.designsystem.theme.components.*
import io.prism.android.libraries.ui.strings.CommonStrings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoneroWalletView(
    state: MoneroWalletState,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    BackButton(onClick = { state.eventSink(MoneroWalletEvents.Back) })
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
        ) {
            if (state.createAction is AsyncAction.Success) {
                IconTitleSubtitleMolecule(
                    iconStyle = BigIcon.Style.Default(CompoundIcons.Lock()),
                    title = "Your Monero Wallet",
                    subTitle = "Wallet created successfully. Please save these credentials in a safe place. If you lose them, you lose your coins."
                )
                
                Spacer(Modifier.height(24.dp))
                
                CredentialBox(label = "Wallet Address", value = state.address)
                CredentialBox(label = "Mnemonic Seed", value = state.mnemonic)
                CredentialBox(label = "Secret View Key", value = state.viewKey)
                CredentialBox(label = "Secret Spend Key", value = state.spendKey)

                Spacer(Modifier.height(24.dp))
                
                Text(
                    text = "Withdraw & Deposit:",
                    style = PRISMTheme.typography.fontBodyMdMedium,
                    color = PRISMTheme.colors.textPrimary
                )
                Text(
                    text = "1. To deposit, share your address.\n2. To withdraw, use the Wallet tab (available after setup) to send XMR to another address.",
                    style = PRISMTheme.typography.fontBodySmRegular,
                    color = PRISMTheme.colors.textSecondary
                )

                Spacer(Modifier.weight(1f))
                Button(
                    text = stringResource(CommonStrings.action_continue),
                    onClick = { state.eventSink(MoneroWalletEvents.Continue) },
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                IconTitleSubtitleMolecule(
                    iconStyle = BigIcon.Style.Default(CompoundIcons.Lock()),
                    title = "Monero (XMR) Wallet",
                    subTitle = "Monero is a privacy-focused cryptocurrency. PRISM includes a built-in XMR wallet for anonymous transactions. Your keys, your coins."
                )
                
                Spacer(Modifier.height(32.dp))
                
                if (state.createAction is AsyncAction.Loading) {
                    CircularProgressIndicator()
                    Spacer(Modifier.height(16.dp))
                    Text("Generating secure keys...")
                } else {
                    Spacer(Modifier.weight(1f))
                    Button(
                        text = "Create XMR Wallet",
                        onClick = { state.eventSink(MoneroWalletEvents.CreateWallet) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(12.dp))
                    OutlinedButton(
                        text = stringResource(CommonStrings.action_skip),
                        onClick = { state.eventSink(MoneroWalletEvents.Skip) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun CredentialBox(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = label,
            style = PRISMTheme.typography.fontBodySmMedium,
            color = PRISMTheme.colors.textPrimary
        )
        Spacer(Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(PRISMTheme.colors.bgSubtlePrimary)
                .border(1.dp, PRISMTheme.colors.borderDisabled, RoundedCornerShape(8.dp))
                .padding(12.dp)
        ) {
            Text(
                text = value,
                style = PRISMTheme.typography.fontBodySmRegular.copy(fontFamily = FontFamily.Monospace),
                color = PRISMTheme.colors.textPrimary
            )
        }
    }
}
