/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.preferences.impl.monerowalletsettings

import android.content.Context
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKey
import im.molly.monero.sdk.WalletDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class MoneroWalletDataStore(
    context: Context,
    private val file: File,
) : WalletDataStore {

    private val encryptedFile: EncryptedFile by lazy {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        EncryptedFile.Builder(
            context,
            file,
            masterKey,
            EncryptedFile.FileEncryptionScheme.AES256_GCM,
        ).build()
    }

    override suspend fun load(): InputStream = withContext(Dispatchers.IO) {
        if (!file.exists()) {
            throw IOException("Wallet file does not exist: ${file.absolutePath}")
        }
        encryptedFile.openFileInput()
    }

    override suspend fun save(writer: (OutputStream) -> Unit, overwrite: Boolean): Unit = withContext(Dispatchers.IO) {
        if (file.exists() && !overwrite) {
            throw IOException("Wallet file already exists and overwrite=false")
        }
        if (file.exists()) {
            file.delete()
        }
        encryptedFile.openFileOutput().use(writer)
    }
}
