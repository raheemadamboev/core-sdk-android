package xyz.teamgravity.coresdkandroid.preferences

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import timber.log.Timber
import xyz.teamgravity.coresdkandroid.crypto.CryptoManager
import java.io.IOException

class Preferences(
    private val crypto: CryptoManager,
    context: Context
) {

    private companion object {
        const val NAME = "CoreSDKAndroid_Preferences"
    }

    private val Context.store by preferencesDataStore(NAME)
    private val store = context.store

    private suspend fun upsertStringImp(
        key: String,
        value: String?,
        encrypted: Boolean
    ) {
        withContext(Dispatchers.IO) {
            val preferencesKey = stringPreferencesKey(key)
            if (value == null) {
                store.edit { it.remove(preferencesKey) }
                return@withContext
            }

            val processedValue = if (encrypted) crypto.encrypt(value) else value
            if (processedValue == null) {
                Timber.e("upsertStringImp(): value is null after encrypting! Encryption error. Aborted the operation. Original value: <$value>.")
                return@withContext
            }

            store.edit { it[preferencesKey] = processedValue }
        }
    }

    private suspend fun upsertIntImp(
        key: String,
        value: Int?,
        encrypted: Boolean
    ) {
        if (encrypted) {
            upsertStringImp(
                key = key,
                value = value?.toString(),
                encrypted = true
            )
        } else {
            withContext(Dispatchers.IO) {
                store.edit { preferences ->
                    val preferencesKey = intPreferencesKey(key)
                    if (value == null) {
                        preferences.remove(preferencesKey)
                    } else {
                        preferences[preferencesKey] = value
                    }
                }
            }
        }
    }

    private suspend fun upsertLongImp(
        key: String,
        value: Long?,
        encrypted: Boolean
    ) {
        if (encrypted) {
            upsertStringImp(
                key = key,
                value = value?.toString(),
                encrypted = true
            )
        } else {
            withContext(Dispatchers.IO) {
                store.edit { preferences ->
                    val preferencesKey = longPreferencesKey(key)
                    if (value == null) {
                        preferences.remove(preferencesKey)
                    } else {
                        preferences[preferencesKey] = value
                    }
                }
            }
        }
    }

    private suspend fun upsertBooleanImp(
        key: String,
        value: Boolean?,
        encrypted: Boolean
    ) {
        if (encrypted) {
            upsertStringImp(
                key = key,
                value = value?.toString(),
                encrypted = true
            )
        } else {
            withContext(Dispatchers.IO) {
                store.edit { preferences ->
                    val preferencesKey = booleanPreferencesKey(key)
                    if (value == null) {
                        preferences.remove(preferencesKey)
                    } else {
                        preferences[preferencesKey] = value
                    }
                }
            }
        }
    }

    private fun getStringImp(
        key: String,
        default: String?,
        encrypted: Boolean
    ): Flow<String?> {
        return store.data
            .catch { emit(handleIOException(it)) }
            .map { preferences ->
                val value = preferences[stringPreferencesKey(key)] ?: return@map default
                return@map if (encrypted) (crypto.decrypt(value) ?: default) else value
            }.flowOn(Dispatchers.IO)
    }


    private fun getIntImp(
        key: String,
        default: Int?,
        encrypted: Boolean
    ): Flow<Int?> {
        return if (encrypted) {
            getStringImp(
                key = key,
                default = default?.toString(),
                encrypted = true
            ).map { it?.toInt() ?: default }
        } else {
            store.data
                .catch { emit(handleIOException(it)) }
                .map { it[intPreferencesKey(key)] ?: default }
        }
    }

    private fun getLongImp(
        key: String,
        default: Long?,
        encrypted: Boolean
    ): Flow<Long?> {
        return if (encrypted) {
            getStringImp(
                key = key,
                default = default?.toString(),
                encrypted = true
            ).map { it?.toLong() ?: default }
        } else {
            store.data
                .catch { emit(handleIOException(it)) }
                .map { it[longPreferencesKey(key)] ?: default }
        }
    }

    private fun getBooleanImp(
        key: String,
        default: Boolean?,
        encrypted: Boolean
    ): Flow<Boolean?> {
        return if (encrypted) {
            getStringImp(
                key = key,
                default = default?.toString(),
                encrypted = true
            ).map { it?.toBooleanStrictOrNull() ?: default }
        } else {
            store.data
                .catch { emit(handleIOException(it)) }
                .map { it[booleanPreferencesKey(key)] ?: default }
        }
    }

    private fun handleIOException(t: Throwable): Preferences {
        return if (t is IOException) emptyPreferences() else throw t
    }

    ///////////////////////////////////////////////////////////////////////////
    // Upsert
    ///////////////////////////////////////////////////////////////////////////

    suspend fun upsertString(
        key: PreferencesKey,
        value: String?
    ) {
        upsertStringImp(
            key = key.key,
            value = value,
            encrypted = key.encrypted
        )
    }

    suspend fun upsertInt(
        key: PreferencesKey,
        value: Int?
    ) {
        upsertIntImp(
            key = key.key,
            value = value,
            encrypted = key.encrypted
        )
    }

    suspend fun upsertLong(
        key: PreferencesKey,
        value: Long?
    ) {
        upsertLongImp(
            key = key.key,
            value = value,
            encrypted = key.encrypted
        )
    }

    suspend fun upsertBoolean(
        key: PreferencesKey,
        value: Boolean?
    ) {
        upsertBooleanImp(
            key = key.key,
            value = value,
            encrypted = key.encrypted
        )
    }

    ///////////////////////////////////////////////////////////////////////////
    // Get
    ///////////////////////////////////////////////////////////////////////////

    fun getString(
        key: PreferencesKey,
        default: String? = key.default as? String
    ): Flow<String?> {
        return getStringImp(
            key = key.key,
            default = default,
            encrypted = key.encrypted
        )
    }

    fun getInt(
        key: PreferencesKey,
        default: Int? = key.default as? Int
    ): Flow<Int?> {
        return getIntImp(
            key = key.key,
            default = default,
            encrypted = key.encrypted
        )
    }

    fun getLong(
        key: PreferencesKey,
        default: Long? = key.default as? Long
    ): Flow<Long?> {
        return getLongImp(
            key = key.key,
            default = default,
            encrypted = key.encrypted
        )
    }

    fun getBoolean(
        key: PreferencesKey,
        default: Boolean? = key.default as? Boolean
    ): Flow<Boolean?> {
        return getBooleanImp(
            key = key.key,
            default = default,
            encrypted = key.encrypted
        )
    }
}