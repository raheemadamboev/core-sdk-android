package xyz.teamgravity.coresdkandroid.android

import android.os.Bundle
import android.os.Parcelable
import java.io.Serializable

/**
 * Retrieves a [Serializable] value from the Bundle, handling API level differences for `getSerializable`.
 *
 * @param key key of value.
 *
 * @return serializable object, null if value doesn't exist.
 */
inline fun <reified T : Serializable> Bundle.getSerializableCompat(key: String): T? {
    return if (BuildUtil.atLeast33()) getSerializable(key, T::class.java)
    else @Suppress("DEPRECATION") getSerializable(key) as T?
}

/**
 * Retrieves a [Parcelable] value from the Bundle, handling API level differences for `getParcelable`.
 *
 * @param key key of value.
 *
 * @return parcelable object, null if value doesn't exist.
 */
inline fun <reified T : Parcelable> Bundle.getParcelableCompat(key: String): T? {
    return if (BuildUtil.atLeast33()) getParcelable(key, T::class.java)
    else @Suppress("DEPRECATION") getParcelable(key) as T?
}