package xyz.teamgravity.coresdkandroid.preferences

import kotlinx.coroutines.flow.Flow

/**
 * Gravity Preferences: typed key-value storage with optional encryption per key.
 */
interface Preferences {

    ///////////////////////////////////////////////////////////////////////////
    // Upsert
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Stores the String [value] under [key], encrypted when the key says so. A null value removes the key.
     *
     * @param key
     * Key to write.
     * @param value
     * String to store, or null to remove it.
     */
    suspend fun upsertString(
        key: PreferencesKey,
        value: String?
    )

    /**
     * Stores the Int [value] under [key], encrypted when the key says so. A null value removes the key.
     *
     * @param key
     * Key to write.
     * @param value
     * Int to store, or null to remove it.
     */
    suspend fun upsertInt(
        key: PreferencesKey,
        value: Int?
    )

    /**
     * Stores the Long [value] under [key], encrypted when the key says so. A null value removes the key.
     *
     * @param key
     * Key to write.
     * @param value
     * Long to store, or null to remove it.
     */
    suspend fun upsertLong(
        key: PreferencesKey,
        value: Long?
    )

    /**
     * Stores the Boolean [value] under [key], encrypted when the key says so. A null value removes the key.
     *
     * @param key
     * Key to write.
     * @param value
     * Boolean to store, or null to remove it.
     */
    suspend fun upsertBoolean(
        key: PreferencesKey,
        value: Boolean?
    )

    ///////////////////////////////////////////////////////////////////////////
    // Get
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Observes the String stored under [key].
     *
     * @param key
     * Key to read.
     * @param default
     * Value emitted when nothing is stored. The key's own default by default.
     * @return Flow that emits the stored value after every change, or [default] when nothing is stored.
     */
    fun getString(
        key: PreferencesKey,
        default: String? = key.default as? String
    ): Flow<String?>

    /**
     * Observes the Int stored under [key].
     *
     * @param key
     * Key to read.
     * @param default
     * Value emitted when nothing is stored. The key's own default by default.
     * @return Flow that emits the stored value after every change, or [default] when nothing is stored.
     */
    fun getInt(
        key: PreferencesKey,
        default: Int? = key.default as? Int
    ): Flow<Int?>

    /**
     * Observes the Long stored under [key].
     *
     * @param key
     * Key to read.
     * @param default
     * Value emitted when nothing is stored. The key's own default by default.
     * @return Flow that emits the stored value after every change, or [default] when nothing is stored.
     */
    fun getLong(
        key: PreferencesKey,
        default: Long? = key.default as? Long
    ): Flow<Long?>

    /**
     * Observes the Boolean stored under [key].
     *
     * @param key
     * Key to read.
     * @param default
     * Value emitted when nothing is stored. The key's own default by default.
     * @return Flow that emits the stored value after every change, or [default] when nothing is stored.
     */
    fun getBoolean(
        key: PreferencesKey,
        default: Boolean? = key.default as? Boolean
    ): Flow<Boolean?>
}
