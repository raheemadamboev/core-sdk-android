package xyz.teamgravity.coresdkandroid.crypto

/**
 * Encrypts and decrypts strings.
 */
interface CryptoManager {

    ///////////////////////////////////////////////////////////////////////////
    // API
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Encrypts [value].
     *
     * @param value
     * Plain text to encrypt.
     * @return Encrypted text that [decrypt] turns back into [value], or null when encryption fails.
     */
    suspend fun encrypt(value: String): String?

    /**
     * Decrypts a [value] made by [encrypt].
     *
     * @param value
     * Text returned by [encrypt].
     * @return The original plain text, or null when decryption fails.
     */
    suspend fun decrypt(value: String): String?
}
