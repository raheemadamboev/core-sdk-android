package xyz.teamgravity.coresdkandroid.android

import android.app.Activity
import android.view.WindowInsets
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat

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