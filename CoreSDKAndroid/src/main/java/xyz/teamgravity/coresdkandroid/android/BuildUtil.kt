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
    fun atLeastTiramisu(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
    }
}