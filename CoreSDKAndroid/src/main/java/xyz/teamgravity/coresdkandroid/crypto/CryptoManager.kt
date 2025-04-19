package xyz.teamgravity.coresdkandroid.crypto

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import kotlin.coroutines.cancellation.CancellationException
import kotlin.io.encoding.Base64

class CryptoManager {

    private companion object {
        const val ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
        const val BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC
        const val PADDING = KeyProperties.ENCRYPTION_PADDING_PKCS7
        const val TRANSFORMATION = "$ALGORITHM/$BLOCK_MODE/$PADDING"
        const val KEYSTORE_ALIAS = "secret"
        const val ANDROID_KEYSTORE = "AndroidKeyStore"
        val INITIAL_VECTOR: ByteArray = byteArrayOf(0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00)
    }

    private val keystore: KeyStore = getKeyStore()

    private fun encryptCipher(): Cipher {
        return Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.ENCRYPT_MODE, getKey(), IvParameterSpec(INITIAL_VECTOR))
        }
    }

    private fun decryptCipher(): Cipher {
        return Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.DECRYPT_MODE, getKey(), IvParameterSpec(INITIAL_VECTOR))
        }
    }

    private fun getKeyStore(): KeyStore {
        val store = KeyStore.getInstance(ANDROID_KEYSTORE)
        store.load(null)
        return store
    }

    private fun getKey(): SecretKey {
        val key = keystore.getEntry(KEYSTORE_ALIAS, null) as? KeyStore.SecretKeyEntry
        return key?.secretKey ?: createKey()
    }

    private fun createKey(): SecretKey {
        return KeyGenerator.getInstance(ALGORITHM).apply {
            init(
                KeyGenParameterSpec.Builder(KEYSTORE_ALIAS, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(BLOCK_MODE)
                    .setEncryptionPaddings(PADDING)
                    .setUserAuthenticationRequired(false)
                    .setRandomizedEncryptionRequired(true)
                    .build()
            )
        }.generateKey()
    }

    ///////////////////////////////////////////////////////////////////////////
    // API
    ///////////////////////////////////////////////////////////////////////////

    suspend fun encrypt(value: String): String? {
        return withContext(Dispatchers.Default) {
            try {
                val encryptedValue = encryptCipher().doFinal(value.toByteArray())
                return@withContext Base64.encode(encryptedValue)
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                Timber.e(e)
                return@withContext null
            }
        }
    }

    suspend fun decrypt(value: String): String? {
        return withContext(Dispatchers.Default) {
            try {
                val decodedValue = Base64.decode(value)
                val decryptedValue = decryptCipher().doFinal(decodedValue)
                return@withContext String(decryptedValue)
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                Timber.e(e)
                return@withContext null
            }
        }
    }
}