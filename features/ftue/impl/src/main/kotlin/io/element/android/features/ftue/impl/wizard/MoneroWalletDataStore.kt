package io.prism.android.features.ftue.impl.wizard

import im.molly.monero.sdk.WalletDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class MoneroWalletDataStore(private val file: File) : WalletDataStore {
    override suspend fun load(): java.io.InputStream = withContext(Dispatchers.IO) {
        FileInputStream(file)
    }

    override suspend fun save(writer: (java.io.OutputStream) -> Unit, overwrite: Boolean): Unit = withContext(Dispatchers.IO) {
        if (overwrite) {
            val tempFile = File(file.parentFile, "${file.name}.tmp")
            FileOutputStream(tempFile).use(writer)
            tempFile.renameTo(file)
        } else {
            FileOutputStream(file).use(writer)
        }
    }
}
