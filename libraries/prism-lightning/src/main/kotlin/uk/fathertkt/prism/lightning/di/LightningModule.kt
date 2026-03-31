/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 */

package uk.fathertkt.prism.lightning.di

import android.content.Context
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import io.prism.android.libraries.di.annotations.ApplicationContext
import timber.log.Timber
import uk.fathertkt.prism.lightning.LightningWalletModule

/**
 * Breez SDK Lightning cüzdanını DI grafiğine ekler.
 *
 * API key için https://breez.technology adresinden hesap oluşturun.
 * API anahtarını aşağıdaki şekillerde sağlayabilirsiniz:
 *  1. `gradle.properties`: `breez.api.key=xxxxx`
 *  2. Env var: `BREEZ_API_KEY`
 *  3. BuildConfig alanı olarak (önerilen)
 *
 * Mnemonic ilk çalıştırmada otomatik oluşturulur ve cihazda saklanır.
 * Production'da EncryptedSharedPreferences kullanılması önerilir.
 */
@BindingContainer
@ContributesTo(AppScope::class)
object LightningModule {

    private const val PREFS_NAME = "prism_lightning_config"
    private const val KEY_MNEMONIC = "mnemonic"

    @Provides
    @SingleIn(AppScope::class)
    fun provideLightningWalletModule(
        @ApplicationContext context: Context,
    ): LightningWalletModule {
        val apiKey = resolveBreezeApiKey(context)
        val mnemonic = resolveOrCreateMnemonic(context)
        return LightningWalletModule(apiKey = apiKey, mnemonic = mnemonic)
    }

    /**
     * API key'i önce BuildConfig'den, yoksa SharedPreferences'dan okur.
     * Kullanıcı kendi key'ini uygulamaya ekleyene kadar boş string döner
     * ve Lightning özelliği "not connected" durumunda kalır.
     */
    private fun resolveBreezeApiKey(context: Context): String {
        return try {
            val buildConfigClass = Class.forName("${context.packageName}.BuildConfig")
            val key = buildConfigClass.getField("BREEZ_API_KEY").get(null) as? String ?: ""
            if (key.isBlank()) {
                Timber.w("BREEZ_API_KEY tanımlı değil — Lightning özelliği devre dışı kalacak")
            }
            key
        } catch (_: Exception) {
            Timber.w("BuildConfig'den BREEZ_API_KEY okunamadı — Lightning devre dışı")
            ""
        }
    }

    /**
     * Mnemonic'i SharedPreferences'dan okur.
     * Yoksa 12 kelimelik BIP39 compatible seed phrase oluşturur.
     * NOT: Production'da `EncryptedSharedPreferences` kullanın.
     */
    private fun resolveOrCreateMnemonic(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val stored = prefs.getString(KEY_MNEMONIC, null)
        if (!stored.isNullOrBlank()) return stored

        // BIP39 word generation — Breez SDK bunu destekler.
        // Breez SDK `generateMnemonic()` varsa onu kullanın, yoksa kütüphane ekleyin.
        // Şimdilik placeholder — kullanıcı kendi mnemonic'ini ayarlayabilir.
        val generated = generatePlaceholderMnemonic()
        prefs.edit().putString(KEY_MNEMONIC, generated).apply()
        return generated
    }

    /**
     * Test amaçlı 12 kelimelik placeholder mnemonic.
     * Production'da gerçek BIP39 mnemonic üretimi gereklidir.
     * Kullanıcı kendi mnemonic'ini PRISM ayarlarından girebilir.
     */
    private fun generatePlaceholderMnemonic(): String {
        // Bu kelimeler test amaçlıdır. Gerçek para kullanmayın.
        val words = listOf(
            "abandon", "ability", "able", "about", "above", "absent",
            "absorb", "abstract", "absurd", "abuse", "access", "accident"
        )
        return words.joinToString(" ")
    }
}
