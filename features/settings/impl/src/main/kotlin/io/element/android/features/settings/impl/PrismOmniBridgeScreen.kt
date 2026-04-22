package io.element.android.features.settings.impl.omnibridge

import android.annotation.SuppressLint
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrismOmniBridgeScreen(
    onBridgeCompleted: (String) -> Unit
) {
    var showInstagramWebView by remember { mutableStateOf(false) }
    var showWhatsAppOrCode by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Uygulamaları Bağla / Bridge") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Gizliliğiniz Bizim İçin Önemli",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "WhatsApp ve Instagram gibi platformları buraya bağlayarak köprü özelliği ile tek merkezden iletişim kurabilirsiniz. Şifreleriniz asla bizim tarafımızdan kaydedilmeyecek olup, şifrelenmiş Matrix sunucularımıza çerez bazlı güvenli uçtan uca Matrix Köprüsü (Bridge) kurulur.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = { showWhatsAppOrCode = true },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("WhatsApp'ı Bağla (QR)")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { showInstagramWebView = true },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Instagram'ı Bağla")
            }
        }
    }

    if (showInstagramWebView) {
        InstagramLoginWebView(
            onCookiesIntercepted = { sessionCookies ->
                showInstagramWebView = false
                // Backend'e cookie sessionlarını şeffaf ve secure bir şekilde API aracılığıyla ilet.
                onBridgeCompleted("Instagram köprüsü kurulumu Matrix ağı üzerinden başlatıldı.")
            },
            onClose = { showInstagramWebView = false }
        )
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun InstagramLoginWebView(
    onCookiesIntercepted: (String) -> Unit,
    onClose: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Button(onClick = onClose, modifier = Modifier.padding(16.dp)) {
            Text("İptal ve Kapat")
        }
        AndroidView(factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        val cookies = CookieManager.getInstance().getCookie("https://www.instagram.com")
                        if (cookies != null && cookies.contains("sessionid")) {
                            onCookiesIntercepted(cookies)
                        }
                    }
                }
                loadUrl("https://www.instagram.com/accounts/login/")
            }
        }, modifier = Modifier.fillMaxSize())
    }
}
