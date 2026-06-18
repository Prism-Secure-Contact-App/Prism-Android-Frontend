/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 */

package io.prism.android.features.ftue.impl.wizard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import android.graphics.Color as AndroidColor
import android.os.Message
import android.view.View
import android.webkit.ConsoleMessage
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.LaunchedEffect
import io.prism.android.features.ftue.impl.BuildConfig
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import android.content.ClipData
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.prism.android.compound.theme.PRISMTheme
import io.prism.android.libraries.architecture.AsyncAction
import io.prism.android.libraries.designsystem.theme.components.Button
import io.prism.android.libraries.designsystem.theme.components.CircularProgressIndicator
import io.prism.android.libraries.designsystem.theme.components.LinearProgressIndicator
import io.prism.android.libraries.designsystem.theme.components.OutlinedButton
import io.prism.android.libraries.designsystem.theme.components.Text
import io.prism.android.libraries.designsystem.theme.components.TextField
import io.prism.android.libraries.matrix.ui.media.MediaRequestData
import io.prism.android.libraries.ui.strings.CommonStrings

/**
 * Renders the dynamic body of the WhatsApp / Meta wizard screens depending on
 * the current [UiPhase]. Kept as a single composable so the two views stay in
 * lock-step visually.
 */
@Composable
internal fun BridgePhaseContent(
    phase: UiPhase,
    bridgeName: String,
    eventSink: ((BridgeEvents) -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val onSubmitPrompt: (String) -> Unit = { value -> eventSink?.invoke(BridgeEvents.SubmitPrompt(value)); Unit }
    val onWebViewError: (String) -> Unit = { message -> eventSink?.invoke(BridgeEvents.WebViewError(message)); Unit }
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        when (phase) {
            UiPhase.Idle -> Unit

            UiPhase.Connecting -> CenteredProgress(
                label = TextResource.Res(CommonStrings.screen_ftue_bridge_connecting, listOf(bridgeName)),
            )

            is UiPhase.Working -> CenteredProgress(label = phase.message, progress = phase.progress)

            is UiPhase.AwaitingScan -> {
                // QR code from the bot's MXC URL. Prisma's matrixui module exposes
                // `requestData()` to build a Coil request for an MXC source; we use it
                // as Painter inside a square box so the QR scales with the viewport.
                Box(
                    modifier = Modifier
                        .size(260.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White)
                        .border(
                            width = 1.dp,
                            color = PRISMTheme.colors.borderInteractiveSecondary,
                            shape = RoundedCornerShape(8.dp),
                        )
                        .padding(8.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    QrImage(source = phase.qrSource)
                }
                Spacer(Modifier.height(12.dp))
                Text(
                    text = phase.caption.resolve(),
                    style = PRISMTheme.typography.fontBodyMdRegular,
                    color = PRISMTheme.colors.textSecondary,
                    textAlign = TextAlign.Center,
                )
            }

            is UiPhase.AwaitingInput -> InputPrompt(prompt = phase.prompt)

            is UiPhase.AwaitingPairingCode -> PairingCodeContent(code = phase.code, caption = phase.caption, remainingSeconds = phase.remainingSeconds)

            is UiPhase.AwaitingWebView -> CookieWebView(
                phase = phase,
                onCookiesCollected = onSubmitPrompt,
                onWebViewError = onWebViewError,
            )

            is UiPhase.Error -> Text(
                text = phase.message.resolve(),
                style = PRISMTheme.typography.fontBodyMdRegular,
                color = PRISMTheme.colors.textCriticalPrimary,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun CenteredProgress(label: TextResource, progress: Float = -1f) {
    if (progress in 0f..1f) {
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(8.dp))
    } else {
        CircularProgressIndicator()
        Spacer(Modifier.height(12.dp))
    }
    Text(
        text = label.resolve(),
        style = PRISMTheme.typography.fontBodyMdRegular,
        color = PRISMTheme.colors.textSecondary,
        textAlign = TextAlign.Center,
    )
}

@Composable
private fun QrImage(source: io.prism.android.libraries.matrix.api.media.MediaSource) {
    // Coil request through matrixmedia — the project's CoilMediaFetcher knows
    // how to resolve `mxc://` URLs against the active session's auth context.
    val data = MediaRequestData(source = source, kind = MediaRequestData.Kind.Content)
    coil3.compose.AsyncImage(
        model = data,
        contentDescription = stringResource(CommonStrings.screen_ftue_bridge_qr_code_content_description),
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun InputPrompt(prompt: TextResource) {
    Text(
        text = prompt.resolve(),
        style = PRISMTheme.typography.fontBodyMdRegular,
        color = PRISMTheme.colors.textSecondary,
        textAlign = TextAlign.Center,
    )
}

/**
 * Embedded WebView for in-app cookie-based bridge logins (Instagram via mautrix-meta).
 *
 * Loads [phase].url with the bridge-mandated user-agent, watches for [phase].successUrlPattern,
 * then scrapes the named cookies from Android's [CookieManager], serializes them as a JSON
 * object the bridge expects, and emits the result via [onCookiesCollected].
 *
 * We intentionally don't pre-clear cookies — Instagram's checkpoint flow benefits from any
 * existing session. If the user has stale credentials they can use the action row's "Çıkış
 * yap & yeniden gir" button (TODO if requested) or clear app data.
 */
@Composable
private fun CookieWebView(
    phase: UiPhase.AwaitingWebView,
    onCookiesCollected: (String) -> Unit,
    onWebViewError: (String) -> Unit,
) {
    var emitted by remember { mutableStateOf(false) }
    var currentUrl by remember { mutableStateOf(phase.url) }

    // Poll the cookie store every second instead of relying on onPageFinished — Instagram is
    // a single-page app: after submitting the login form it navigates client-side without
    // firing a full page load. The success signal is "all required cookies are now set",
    // which we can read from CookieManager regardless of what the WebView is rendering.
    //
    // CRITICAL FIX: we check multiple URLs because after login Instagram redirects away
    // from /accounts/login/ and cookies may be scoped to the root domain or www.instagram.com
    // rather than the original load URL.
    LaunchedEffect(phase.url) {
        while (!emitted && coroutineContext.isActive) {
            delay(1000L)

            val urlsToCheck = listOf(
                currentUrl,
                "https://www.instagram.com/",
                "https://instagram.com/",
                phase.url,
            ).distinct().filter { it.isNotBlank() }

            for (cookieUrl in urlsToCheck) {
                val rawCookieHeader = CookieManager.getInstance().getCookie(cookieUrl)
                if (rawCookieHeader.isNullOrBlank()) {
                    timber.log.Timber.d("Bridge WebView no cookies yet for %s", cookieUrl)
                    continue
                }
                val cookieHeader = rawCookieHeader
                val parsed = parseCookieHeader(cookieHeader)
                val collected = LinkedHashMap<String, String>(phase.cookieNames.size)
                var missing = false
                for (name in phase.cookieNames) {
                    val v = parsed[name]
                    if (v.isNullOrBlank()) { missing = true; break }
                    collected[name] = v
                }
                if (missing) continue
                emitted = true
                timber.log.Timber.d("Bridge WebView cookies collected from %s: %s", cookieUrl, collected.keys)
                onCookiesCollected(buildCookieJson(collected))
                break
            }
        }
    }

    Text(
        text = phase.caption.resolve(),
        style = PRISMTheme.typography.fontBodyMdRegular,
        color = PRISMTheme.colors.textSecondary,
        textAlign = TextAlign.Center,
    )
    Spacer(Modifier.height(12.dp))
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 520.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White) // Instagram renders white; avoids brief black flash on load
            .border(
                width = 1.dp,
                color = PRISMTheme.colors.borderInteractiveSecondary,
                shape = RoundedCornerShape(12.dp),
            ),
    ) {
        AndroidView(
            factory = { ctx ->
                // Enable Chrome-DevTools inspection only in debug builds.
                WebView.setWebContentsDebuggingEnabled(BuildConfig.DEBUG)

                WebView(ctx).apply {
                    // Explicit white background — without this, Compose AndroidView wraps the
                    // WebView in a layer that initially shows the parent (dark) theme background
                    // until first paint, which the user perceives as "black screen on login click".
                    setBackgroundColor(AndroidColor.WHITE)

                    // FIX: Instagram login page flickers with software rendering.
                    // Keep hardware acceleration (default) and rely on white background
                    // to avoid the brief black flash on load.
                    settings.apply {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        userAgentString = phase.userAgent
                        loadsImagesAutomatically = true
                        useWideViewPort = true
                        loadWithOverviewMode = true
                        // Instagram's login flow occasionally opens a child window. Without
                        // multi-window support the click silently leaves a blank view.
                        javaScriptCanOpenWindowsAutomatically = true
                        setSupportMultipleWindows(true)
                        mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
                        mediaPlaybackRequiresUserGesture = false
                        cacheMode = WebSettings.LOAD_DEFAULT
                    }

                    CookieManager.getInstance().setAcceptCookie(true)
                    CookieManager.getInstance().setAcceptThirdPartyCookies(this, false)

                    webViewClient = object : WebViewClient() {
                        // Instagram pages link to deep-links / "Open in app" (instagram://, intent://, fb://, mailto:).
                        // Default WebView would try to load them and show "web page not available" error.
                        // We simply ignore non-http URLs so the user stays on the login flow.
                        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                            val url = request?.url?.toString() ?: return false
                            return if (!url.startsWith("http://") && !url.startsWith("https://")) {
                                timber.log.Timber.d("Bridge WebView ignoring non-http URL: %s", url)
                                true
                            } else {
                                false
                            }
                        }

                        override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                            super.onPageStarted(view, url, favicon)
                            timber.log.Timber.d("Bridge WebView page started: %s", url)
                        }

                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            currentUrl = url ?: phase.url
                            // Force cookie sync so CookieManager sees them immediately
                            CookieManager.getInstance().flush()
                            timber.log.Timber.d("Bridge WebView page finished: %s", url)
                        }

                        // Loud logging on resource errors so we can diagnose login failures from logcat.
                        // Only treat main-frame errors as fatal; ignore sub-resource failures (CSS/JS)
                        // so a single missing asset does not kill the whole login flow.
                        override fun onReceivedError(
                            view: WebView?,
                            request: WebResourceRequest?,
                            error: WebResourceError?,
                        ) {
                            if (request?.isForMainFrame != true) {
                                timber.log.Timber.d(
                                    "Bridge WebView sub-resource error (%d): %s",
                                    error?.errorCode ?: -1,
                                    error?.description ?: "unknown"
                                )
                                return
                            }
                            onWebViewError(
                                "Login page failed to load: ${error?.description ?: "Unknown error"} (${error?.errorCode})"
                            )
                        }
                    }

                    webChromeClient = object : WebChromeClient() {
                        // Funnel popup attempts (login form sometimes opens child window for 2FA)
                        // back into the same WebView instead of letting them die silently.
                        override fun onCreateWindow(
                            view: WebView?,
                            isDialog: Boolean,
                            isUserGesture: Boolean,
                            resultMsg: Message?,
                        ): Boolean {
                            val transport = resultMsg?.obj as? WebView.WebViewTransport ?: return false
                            transport.webView = view
                            resultMsg.sendToTarget()
                            return true
                        }

                        override fun onProgressChanged(view: WebView?, newProgress: Int) {
                            super.onProgressChanged(view, newProgress)
                            if (newProgress == 100) {
                                CookieManager.getInstance().flush()
                            }
                            timber.log.Timber.d("Bridge WebView loading progress: %d%%", newProgress)
                        }

                        // Forward JS console output to logcat so we can debug black-screen pages.
                        override fun onConsoleMessage(message: ConsoleMessage?): Boolean {
                            message ?: return false
                            timber.log.Timber.d(
                                "Bridge WebView [%s] %s:%d - %s",
                                message.messageLevel(),
                                message.sourceId(),
                                message.lineNumber(),
                                message.message(),
                            )
                            return true
                        }
                    }

                    loadUrl(phase.url)
                }
            },
        )
    }
}

private fun parseCookieHeader(header: String): Map<String, String> =
    header.split(";").mapNotNull { token ->
        val eq = token.indexOf('=')
        if (eq <= 0) return@mapNotNull null
        token.substring(0, eq).trim() to token.substring(eq + 1).trim()
    }.toMap()

private fun buildCookieJson(cookies: Map<String, String>): String =
    cookies.entries.joinToString(prefix = "{", postfix = "}") { (k, v) ->
        "\"${k.escapeJson()}\":\"${v.escapeJson()}\""
    }

private fun String.escapeJson(): String = buildString(length + 2) {
    for (c in this@escapeJson) when (c) {
        '\\' -> append("\\\\")
        '"' -> append("\\\"")
        '\n' -> append("\\n")
        '\r' -> append("\\r")
        '\t' -> append("\\t")
        else -> append(c)
    }
}

@Composable
private fun PairingCodeContent(code: String, caption: TextResource, remainingSeconds: Int = -1) {
    val clipboard = LocalClipboard.current
    val scope = rememberCoroutineScope()
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(PRISMTheme.colors.bgSubtleSecondary)
            .border(
                width = 1.dp,
                color = PRISMTheme.colors.borderInteractiveSecondary,
                shape = RoundedCornerShape(12.dp),
            )
            .padding(horizontal = 24.dp, vertical = 20.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = code,
            style = PRISMTheme.typography.fontHeadingLgBold.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 36.sp,
                letterSpacing = 4.sp,
            ),
            color = PRISMTheme.colors.textPrimary,
            textAlign = TextAlign.Center,
        )
    }
    Spacer(Modifier.height(12.dp))
    OutlinedButton(
        text = stringResource(CommonStrings.screen_ftue_bridge_copy_code),
        onClick = {
            scope.launch {
                clipboard.setClipEntry(ClipEntry(ClipData.newPlainText("PRISM bridge pairing code", code)))
            }
        },
        modifier = Modifier.fillMaxWidth(),
    )
    Spacer(Modifier.height(12.dp))
    if (remainingSeconds >= 0) {
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(CommonStrings.screen_ftue_bridge_time_remaining, remainingSeconds),
            style = PRISMTheme.typography.fontBodySmMedium,
            color = PRISMTheme.colors.textSecondary,
            textAlign = TextAlign.Center,
        )
    }
    Spacer(Modifier.height(12.dp))
    Text(
        text = caption.resolve(),
        style = PRISMTheme.typography.fontBodyMdRegular,
        color = PRISMTheme.colors.textSecondary,
        textAlign = TextAlign.Center,
    )
}

/**
 * Action area at the bottom of the screen. Adapts the button set to the
 * current connection phase so we never show a stale "Connect" while a
 * scan is mid-flight.
 */
@Composable
internal fun BridgeActionRow(
    state: BridgeState,
    primaryLabel: TextResource,
) {
    val isLoading = state.connectAction is AsyncAction.Loading &&
        state.phase !is UiPhase.AwaitingScan &&
        state.phase !is UiPhase.AwaitingInput &&
        state.phase !is UiPhase.AwaitingPairingCode &&
        state.phase !is UiPhase.AwaitingWebView

    Column(modifier = Modifier.fillMaxWidth()) {
        when (val phase = state.phase) {
            is UiPhase.AwaitingInput -> {
                // Pre-fill with the flow-supplied seed (e.g. "+" for phone numbers) so users
                // on numeric keyboards never have to hunt for the plus sign.
                var input by remember(phase.initialValue) { mutableStateOf(phase.initialValue) }
                TextField(
                    value = input,
                    onValueChange = { input = it },
                    label = phase.inputLabel.resolve(),
                    placeholder = phase.inputPlaceholder.resolve(),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = phase.keyboardType != androidx.compose.ui.text.input.KeyboardType.Text,
                    keyboardOptions = KeyboardOptions(keyboardType = phase.keyboardType),
                )
                Spacer(Modifier.height(12.dp))
                Button(
                    text = stringResource(CommonStrings.screen_ftue_bridge_send),
                    onClick = { state.eventSink(BridgeEvents.SubmitPrompt(input)) },
                    enabled = input.isNotBlank(),
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            is UiPhase.AwaitingPairingCode -> {
                // Code is shown in BridgePhaseContent above; here we just give the user
                // a way to bail if they entered the wrong number / WhatsApp rejected it.
                OutlinedButton(
                    text = stringResource(CommonStrings.screen_ftue_bridge_cancel),
                    onClick = { state.eventSink(BridgeEvents.Skip) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            is UiPhase.AwaitingWebView -> {
                // The cookie WebView submits automatically on success; the only manual control
                // we expose is "abort" so the user can leave a stuck/checkpoint flow.
                OutlinedButton(
                    text = stringResource(CommonStrings.screen_ftue_bridge_cancel),
                    onClick = { state.eventSink(BridgeEvents.Skip) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            is UiPhase.AwaitingScan -> {
                // Scan UX is purely passive — show only an "Cancel" so the user can bail.
                OutlinedButton(
                    text = stringResource(CommonStrings.screen_ftue_bridge_cancel),
                    onClick = { state.eventSink(BridgeEvents.Skip) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            else -> {
                Button(
                    text = primaryLabel.resolve(),
                    onClick = { state.eventSink(BridgeEvents.Connect) },
                    showProgress = isLoading,
                    enabled = !isLoading,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(8.dp))
                OutlinedButton(
                    text = stringResource(CommonStrings.screen_ftue_bridge_skip_for_now),
                    onClick = { state.eventSink(BridgeEvents.Skip) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}
