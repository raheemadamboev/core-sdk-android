package xyz.teamgravity.coresdkandroid.android

import android.annotation.SuppressLint
import android.app.UiModeManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import timber.log.Timber
import java.util.Locale

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

/**
 * Applies [locale] to this `Context`'s resources in place via the deprecated
 * `Resources.updateConfiguration` API. Mutates the existing resources, so
 * subsequent string/resource lookups on this `Context` use the new language.
 *
 * @param locale a BCP 47 language tag (e.g. `"en"`, `"fr"`, `"zh-Hant"`).
 */
fun Context.setLocale(locale: String) {
    val locale = Locale.forLanguageTag(locale)
    Locale.setDefault(locale)
    val config = Configuration(resources.configuration)
    config.setLocale(locale)
    @Suppress("DEPRECATION")
    resources.updateConfiguration(config, resources.displayMetrics)
}

/**
 * Returns a new `Context` configured with [locale], leaving the receiver
 * untouched. Intended for use in `attachBaseContext` to localize an
 * `Activity` or `Application`.
 *
 * @param locale a BCP 47 language tag (e.g. `"en"`, `"fr"`, `"zh-Hant"`).
 */
fun Context.changeLocale(locale: String): Context {
    val locale = Locale.forLanguageTag(locale)
    Locale.setDefault(locale)
    val config = Configuration(resources.configuration)
    config.setLocale(locale)
    return createConfigurationContext(config)
}

@DrawableRes
@SuppressLint("DiscouragedApi")
fun Context.getDrawableResourceIdFromName(name: String): Int {
    return resources.getIdentifier(name.removeExtension(), "drawable", packageName)
}

@RawRes
@SuppressLint("DiscouragedApi")
fun Context.getRawResourceIdFromName(name: String): Int {
    return resources.getIdentifier(name.removeExtension(), "raw", packageName)
}

fun Context.getDrawableFromName(name: String): Drawable? {
    return try {
        ContextCompat.getDrawable(this, getDrawableResourceIdFromName(name))
    } catch (e: Resources.NotFoundException) {
        Timber.e(e)
        null
    }
}