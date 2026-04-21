package xyz.teamgravity.coresdkandroid.android

import android.app.UiModeManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import androidx.core.content.getSystemService
import timber.log.Timber

/**
 * Safely starts activity. If activity is not found or not permitted, calls [onError] lambda.
 *
 * @param intent
 * Intent to start.
 * @param onError
 * Lambda that is called when starting activity is not found or not permitted.
 */
fun Context.safelyStartActivity(
    intent: Intent,
    onError: () -> Unit = {}
) {
    try {
        startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        Timber.e(e)
        onError()
    } catch (e: SecurityException) {
        Timber.e(e)
        onError()
    }
}

/**
 * Returns `true` if the app is currently in dark mode (respects `AppCompatDelegate` overrides).
 */
fun Context.isDarkMode(): Boolean {
    return (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
}

/**
 * Returns `true` if the system-wide dark mode is enabled, ignoring any app-level override.
 */
fun Context.isSystemInDarkMode(): Boolean {
    val uiModeManager = getSystemService<UiModeManager>()
    return uiModeManager?.nightMode == UiModeManager.MODE_NIGHT_YES
}