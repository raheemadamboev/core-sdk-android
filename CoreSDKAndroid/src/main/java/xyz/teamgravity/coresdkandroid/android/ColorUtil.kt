package xyz.teamgravity.coresdkandroid.android

import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.core.graphics.toColorInt

object ColorUtil {

    /**
     * Tries to parse color. If passed [color] is null or invalid, it executes the [onDefault] lambda.
     */
    @ColorInt
    private fun safelyParseColor(
        color: String?,
        @ColorInt onDefault: () -> Int
    ): Int {
        if (color.isNullOrBlank()) return onDefault()
        return try {
            color.toColorInt()
        } catch (e: IllegalArgumentException) {
            onDefault()
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // API
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Parses the color string, and returns the corresponding color-int.
     * Parses the [color] if it is not null and valid. If [color] is null or invalid, it tries to parse [default].
     * If [default] is null or invalid after all, it just returns [Color.TRANSPARENT].
     *
     * @param color
     * String color that needs to be parsed.
     * @param default
     * String color that needs to be parsed if [color] is null or invalid.
     */
    @ColorInt
    fun parseColor(
        color: String?,
        default: String? = null
    ): Int = safelyParseColor(
        color = color,
        onDefault = {
            safelyParseColor(
                color = default,
                onDefault = { Color.TRANSPARENT }
            )
        }
    )
}