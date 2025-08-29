package xyz.teamgravity.coresdkandroid.android

import android.content.res.Resources
import kotlin.math.roundToInt

object SizeUtil {

    /**
     * Converts DP to PX.
     */
    fun dpToPx(dp: Float): Int {
        return (dp * Resources.getSystem().displayMetrics.density).roundToInt()
    }

    /**
     * Converts DP to PX.
     */
    fun dpToPx(dp: Int): Int {
        return dpToPx(dp.toFloat())
    }

    /**
     * Converts PX to DP.
     */
    fun pxToDp(px: Float): Float {
        return px / Resources.getSystem().displayMetrics.density
    }

    /**
     * Converts PX to DP.
     */
    fun pxToDp(px: Int): Float {
        return pxToDp(px.toFloat())
    }
}