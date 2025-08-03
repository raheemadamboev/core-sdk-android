package xyz.teamgravity.coresdkandroid.android

import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast

object BuildUtil {

    ///////////////////////////////////////////////////////////////////////////
    // API
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Checks if device is running at least on Oreo (Android 8.1).
     *
     * @return true if device is running on API 27 (Android 8.1) or above, false otherwise.
     */
    @ChecksSdkIntAtLeast(Build.VERSION_CODES.O_MR1)
    fun atLeast27(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1
    }

    /**
     * Checks if device is running at least on Pie (Android 9).
     *
     * @return true if device is running on API 28 (Android 9) or above, false otherwise.
     */
    @ChecksSdkIntAtLeast(Build.VERSION_CODES.P)
    fun atLeast28(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
    }

    /**
     * Checks if device is running at least on Quince Tart (Android 10).
     *
     * @return true if device is running on API 29 (Android 10) or above, false otherwise.
     */
    @ChecksSdkIntAtLeast(Build.VERSION_CODES.Q)
    fun atLeast29(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
    }

    /**
     * Checks if device is running at least on Red Velvet Cake (Android 11).
     *
     * @return true if device is running on API 30 (Android 11) or above, false otherwise.
     */
    @ChecksSdkIntAtLeast(Build.VERSION_CODES.R)
    fun atLeast30(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
    }

    /**
     * Checks if device is running at least on Snow Cone (Android 12).
     *
     * @return true if device is running on API 31 (Android 12) or above, false otherwise.
     */
    @ChecksSdkIntAtLeast(Build.VERSION_CODES.S)
    fun atLeast31(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    }

    /**
     * Checks if device is running at least on Snow Cone (Android 12L).
     *
     * @return true if device is running on API 32 (Android 12L) or above, false otherwise.
     */
    @ChecksSdkIntAtLeast(Build.VERSION_CODES.S_V2)
    fun atLeast32(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.S_V2
    }

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

    /**
     * Checks if device is running at least on Baklava (Android 16).
     *
     * @return true if device is running on API 36 (Android 16) or above, false otherwise.
     */
    @ChecksSdkIntAtLeast(Build.VERSION_CODES.BAKLAVA)
    fun atLeast36(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA
    }
}