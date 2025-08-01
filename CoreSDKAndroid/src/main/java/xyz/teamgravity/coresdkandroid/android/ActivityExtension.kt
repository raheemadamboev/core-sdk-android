package xyz.teamgravity.coresdkandroid.android

import android.app.Activity
import android.view.WindowInsets
import androidx.annotation.AnimRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat

/**
 * Sets status bar and navigation bar color properly according to sdk int.
 *
 * @param color
 * Color resource id.
 */
fun Activity.setStatusNavigationBarColorResource(@ColorRes color: Int) {
    setStatusNavigationBarColor(ContextCompat.getColor(this, color))
}

/**
 * Sets status bar and navigation bar color properly according to sdk int.
 *
 * @param color
 * Color int.
 */
fun Activity.setStatusNavigationBarColor(@ColorInt color: Int) {
    if (BuildUtil.atLeast35()) {
        window.decorView.setOnApplyWindowInsetsListener { view, insets ->
            val statusBarInsets = insets.getInsets(WindowInsets.Type.statusBars())
            view.setBackgroundColor(color)
            view.setPadding(0, statusBarInsets.top, 0, 0)
            insets
        }
    } else {
        @Suppress("DEPRECATION")
        window.statusBarColor = color
        @Suppress("DEPRECATION")
        window.navigationBarColor = color
    }
}

/**
 * Overrides enter transition of activity on devices that run on Android 13 (API 33) or lower.
 *
 * @param enterAnim
 * Enter animation resource id.
 * @param exitAnim
 * Exit animation resource id.
 */
fun Activity.overridePre34EnterTransition(
    @AnimRes enterAnim: Int,
    @AnimRes exitAnim: Int
) {
    if (!BuildUtil.atLeast34()) {
        @Suppress("DEPRECATION")
        overridePendingTransition(enterAnim, exitAnim)
    }
}

/**
 * Overrides enter transition of activity on devices that run on Android 14 (API 34) or higher.
 *
 * Call it in target activity's onCreate function after super.onCreate() and before setContentView().
 *
 * ```kotlin
 * override fun onCreate(savedInstanceState: Bundle?) {
 *   super.onCreate(savedInstanceState)
 *   overridePost34EnterTransition()
 *   setContentView()
 * }
 * ```
 *
 * @param enterAnim
 * Enter animation resource id.
 * @param exitAnim
 * Exit animation resource id.
 */
fun Activity.overridePost34EnterTransition(
    @AnimRes enterAnim: Int,
    @AnimRes exitAnim: Int
) {
    if (BuildUtil.atLeast34()) {
        overrideActivityTransition(Activity.OVERRIDE_TRANSITION_OPEN, enterAnim, exitAnim)
    }
}

/**
 * Sets the status bar and navigation bar to have light content (dark icons).
 */
fun Activity.setLightBars() {
    val window = WindowCompat.getInsetsController(window, window.decorView)
    window.isAppearanceLightStatusBars = true
    window.isAppearanceLightNavigationBars = true
}

/**
 * Sets the status bar and navigation to have dark content (light icons).
 */
fun Activity.setDarkBars() {
    val window = WindowCompat.getInsetsController(window, window.decorView)
    window.isAppearanceLightStatusBars = false
    window.isAppearanceLightNavigationBars = false
}

/**
 * Sets the navigation bar to have transparent background.
 */
fun Activity.setNavigationBarTransparent() {
    if (BuildUtil.atLeast29()) window.isNavigationBarContrastEnforced = false
}