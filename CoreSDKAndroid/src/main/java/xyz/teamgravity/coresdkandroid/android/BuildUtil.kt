package xyz.teamgravity.coresdkandroid.android

import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast

object BuildUtil {

    ///////////////////////////////////////////////////////////////////////////
    // API
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Checks if device is running at least on Tiramisu (Android 13).
     *
     * @return true if device is running on API 33 (Android 13) or above, false otherwise.
     */
    @ChecksSdkIntAtLeast(Build.VERSION_CODES.TIRAMISU)
    fun atLeast33(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
    }

    /**
     * Checks if device is running at least on Upside Down Cake (Android 14).
     *
     * @return true if device is running on API 34 (Android 14) or above, false otherwise.
     */
    @ChecksSdkIntAtLeast(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    fun atLeast34(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE
    }

    /**
     * Checks if device is running at least on Vanilla Ice Cream (Android 15).
     *
     * @return true if device is running on API 35 (Android 15) or above, false otherwise.
     */
    @ChecksSdkIntAtLeast(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    fun atLeast35(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM
    }
}