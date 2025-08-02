package xyz.teamgravity.coresdkandroid.android

import android.content.Intent
import android.os.Parcelable
import java.io.Serializable

/**
 * Retrieves a [Serializable] value from the Intent, handling API level differences for `getSerializable`.
 *
 * @param key key of value.
 *
 * @return serializable object, null if value doesn't exist.
 */
inline fun <reified T : Serializable> Intent.getSerializableCompat(key: String): T? {
    return if (BuildUtil.atLeast33()) getSerializableExtra(key, T::class.java)
    else @Suppress("DEPRECATION") getSerializableExtra(key) as T?
}

/**
 * Retrieves a [Parcelable] value from the Intent, handling API level differences for `getParcelable`.
 *
 * @param key key of value.
 *
 * @return parcelable object, null if value doesn't exist.
 */
inline fun <reified T : Parcelable> Intent.getParcelableCompat(key: String): T? {
    return if (BuildUtil.atLeast33()) getParcelableExtra(key, T::class.java)
    else @Suppress("DEPRECATION") getParcelableExtra(key) as T?
}