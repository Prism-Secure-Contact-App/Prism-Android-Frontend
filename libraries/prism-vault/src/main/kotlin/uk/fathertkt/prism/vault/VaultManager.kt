/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 */

package uk.fathertkt.prism.vault

import android.content.Context
import android.content.SharedPreferences
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.prism.android.libraries.di.annotations.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext

private const val VAULT_PREFS_FILE = "prism_vault_encrypted"
private const val VAULT_ROOMS_KEY = "vault_rooms"

/**
 * PRISM Vault Manager
 *
 * Seçili sohbetleri ana listeden gizleyip biyometrik doğrulama arkasına kilitler.
 * Room ID'leri Android Keystore ile şifrelenmiş [EncryptedSharedPreferences]'ta saklanır.
 *
 * Kullanım akışı:
 *   1. Kullanıcı bir sohbeti kasaya eklemek ister → [addToVault]
 *   2. Ana liste ekranı [vaultRooms] Flow'unu collect eder ve bu room'ları filtreler.
 *   3. Kullanıcı kasayı açmak ister → [promptBiometric]; başarıda kasadaki room'lar gösterilir.
 */
@SingleIn(AppScope::class)
@Inject
class VaultManager(@ApplicationContext private val context: Context) {

    private val masterKey: MasterKey by lazy {
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
    }

    private val encryptedPrefs: SharedPreferences by lazy {
        EncryptedSharedPreferences.create(
            context,
            VAULT_PREFS_FILE,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )
    }

    /** Kasadaki room ID'lerinin canlı akışı. */
    val vaultRooms: Flow<Set<String>> = callbackFlow {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == VAULT_ROOMS_KEY) {
                trySend(getVaultRooms())
            }
        }
        encryptedPrefs.registerOnSharedPreferenceChangeListener(listener)
        trySend(getVaultRooms())
        awaitClose { encryptedPrefs.unregisterOnSharedPreferenceChangeListener(listener) }
    }

    private fun getVaultRooms(): Set<String> {
        return encryptedPrefs.getStringSet(VAULT_ROOMS_KEY, emptySet()) ?: emptySet()
    }

    suspend fun addToVault(roomId: String) {
        withContext(Dispatchers.IO) {
            val updated = getVaultRooms().toMutableSet().apply { add(roomId) }
            encryptedPrefs.edit().putStringSet(VAULT_ROOMS_KEY, updated).apply()
        }
    }

    suspend fun removeFromVault(roomId: String) {
        withContext(Dispatchers.IO) {
            val updated = getVaultRooms().toMutableSet().apply { remove(roomId) }
            encryptedPrefs.edit().putStringSet(VAULT_ROOMS_KEY, updated).apply()
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
                onFailure("Authentication failed")
            }
        }

        val prompt = BiometricPrompt(activity, executor, callback)
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("PRISM Vault")
            .setSubtitle("Authenticate to access secret chats")
            .setAllowedAuthenticators(
                BiometricManager.Authenticators.BIOMETRIC_STRONG or
                    BiometricManager.Authenticators.DEVICE_CREDENTIAL
            )
            .build()

        prompt.authenticate(promptInfo)
    }
}
