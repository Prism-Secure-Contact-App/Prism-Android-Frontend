/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 */

package uk.fathertkt.prism.vault

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.FragmentActivity
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.prism.android.libraries.di.annotations.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.vaultDataStore: DataStore<Preferences> by preferencesDataStore(name = "prism_vault")

/**
 * PRISM Vault Manager
 *
 * Seçili sohbetleri ana listeden gizleyip biyometrik doğrulama arkasına kilitler.
 * Room ID'leri DataStore'a kalıcı olarak yazılır; her uygulama açılışında korunurlar.
 *
 * Kullanım akışı:
 *   1. Kullanıcı bir sohbeti kasaya eklemek ister → [addToVault]
 *   2. Ana liste ekranı [vaultRooms] Flow'unu collect eder ve bu room'ları filtreler.
 *   3. Kullanıcı kasayı açmak ister → [promptBiometric]; başarıda kasadaki room'lar gösterilir.
 */
@SingleIn(AppScope::class)
@Inject
class VaultManager(@ApplicationContext private val context: Context) {

    private val VAULT_ROOMS_KEY = stringSetPreferencesKey("vault_rooms")

    /** Kasadaki room ID'lerinin canlı akışı. */
    val vaultRooms: Flow<Set<String>> = context.vaultDataStore.data
        .map { prefs -> prefs[VAULT_ROOMS_KEY] ?: emptySet() }

    suspend fun addToVault(roomId: String) {
        context.vaultDataStore.edit { prefs ->
            val current = prefs[VAULT_ROOMS_KEY] ?: emptySet()
            prefs[VAULT_ROOMS_KEY] = current + roomId
        }
    }

    suspend fun removeFromVault(roomId: String) {
        context.vaultDataStore.edit { prefs ->
            val current = prefs[VAULT_ROOMS_KEY] ?: emptySet()
            prefs[VAULT_ROOMS_KEY] = current - roomId
        }
    }

    /**
     * Cihazda biyometrik / cihaz kimlik bilgisi doğrulaması kullanılabilir mi?
     */
    fun isBiometricAvailable(): Boolean {
        val manager = BiometricManager.from(context)
        val result = manager.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG or
                BiometricManager.Authenticators.DEVICE_CREDENTIAL
        )
        return result == BiometricManager.BIOMETRIC_SUCCESS
    }

    /**
     * Sistem biyometrik isteğini gösterir.
     *
     * @param activity  BiometricPrompt için gerekli FragmentActivity
     * @param onSuccess Doğrulama başarılıysa çağrılır
     * @param onFailure Doğrulama başarısız veya iptal edilirse hata mesajıyla çağrılır
     */
    fun promptBiometric(
        activity: FragmentActivity,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit,
    ) {
        val executor = ContextCompat.getMainExecutor(activity)
        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                onSuccess()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                onFailure(errString.toString())
            }

            override fun onAuthenticationFailed() {
                onFailure("Kimlik doğrulama başarısız")
            }
        }

        val prompt = BiometricPrompt(activity, executor, callback)
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("PRISM Kasa")
            .setSubtitle("Gizli sohbetlere erişmek için doğrulayın")
            .setAllowedAuthenticators(
                BiometricManager.Authenticators.BIOMETRIC_STRONG or
                    BiometricManager.Authenticators.DEVICE_CREDENTIAL
            )
            .build()

        prompt.authenticate(promptInfo)
    }
}
