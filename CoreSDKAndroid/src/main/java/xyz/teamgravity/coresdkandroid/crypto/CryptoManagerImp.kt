package xyz.teamgravity.coresdkandroid.crypto

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import kotlinx.coroutines.withContext
import timber.log.Timber
import xyz.teamgravity.coresdkandroid.coroutine.dispatcher.DispatcherProvider
import java.nio.ByteBuffer
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import kotlin.coroutines.cancellation.CancellationException
import kotlin.io.encoding.Base64

/**
 * Encrypts with AES in CBC mode and PKCS7 padding, using a key kept in the Android Keystore. Every value gets a fresh
 * IV, and the result is Base64 of the IV size, the IV and the encrypted bytes.
 *
 * @property dispatcher
 * Dispatchers for the encryption work.
 */
class CryptoManagerImp(
    private val dispatcher: DispatcherProvider
) : CryptoManager {

    private companion object {
        const val ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
        const val BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC
        const val PADDING = KeyProperties.ENCRYPTION_PADDING_PKCS7
        const val TRANSFORMATION = "$ALGORITHM/$BLOCK_MODE/$PADDING"
        const val KEYSTORE_ALIAS = "secret"
        const val ANDROID_KEYSTORE = "AndroidKeyStore"
    }

    private val keystore: KeyStore = getKeyStore()

    private fun encryptCipher(): Cipher {
        return Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.ENCRYPT_MODE, getKey())
        }
    }

    private fun decryptCipher(iv: ByteArray): Cipher {
        return Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.DECRYPT_MODE, getKey(), IvParameterSpec(iv))
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

    override suspend fun encrypt(value: String): String? {
        return withContext(dispatcher.default) {
            try {
                val cipher = encryptCipher()
                val encryptedValue = cipher.doFinal(value.toByteArray())
                val ivSize = ByteBuffer.allocate(Int.SIZE_BYTES).putInt(cipher.iv.size).array()
                return@withContext Base64.encode(ivSize + cipher.iv + encryptedValue)
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                Timber.e(e)
                return@withContext null
            }
        }
    }

    override suspend fun decrypt(value: String): String? {
        return withContext(dispatcher.default) {
            try {
                val decodedValue = Base64.decode(value)
                val ivSize = ByteBuffer.wrap(decodedValue.sliceArray(0..<Int.SIZE_BYTES)).asIntBuffer().get()
                val iv = decodedValue.sliceArray(Int.SIZE_BYTES..<(Int.SIZE_BYTES + ivSize))
                val decryptedValue = decryptCipher(iv).doFinal(decodedValue.sliceArray((Int.SIZE_BYTES + ivSize)..<decodedValue.size))
                return@withContext String(decryptedValue)
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                Timber.e(e)
                return@withContext null
            }
        }
    }
}