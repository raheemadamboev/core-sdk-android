package xyz.teamgravity.coresdkandroid.review

import android.app.Activity
import kotlinx.coroutines.flow.Flow

/**
 * Decides when the user is eligible to be asked for an in-app review and launches the review flow.
 */
interface ReviewManager {

    /**
     * Emits [ReviewEvent.Eligible] when the user should be asked for a review.
     */
    val event: Flow<ReviewEvent>

    ///////////////////////////////////////////////////////////////////////////
    // API
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Counts this launch and emits [ReviewEvent.Eligible] through [event] when every condition is met.
     *
     * @param installDays
     * Days that must pass since the first launch. 3 by default.
     * @param launchTimes
     * Launches that must happen. 10 by default.
     * @param remindInterval
     * Days that must pass after the user chose to be reminded later. 1 by default.
     */
    fun monitor(
        installDays: Int = Default.INSTALL_DAYS,
        launchTimes: Int = Default.LAUNCH_TIMES,
        remindInterval: Int = Default.REMIND_INTERVAL
    )

    /**
     * Asks again after the remind interval.
     */
    fun remindLater()

    /**
     * Never asks again.
     */
    fun deny()

    /**
     * Launches the in-app review flow, or opens the app's Play Store page when the flow fails.
     *
     * @param activity
     * Activity that hosts the review flow.
     */
    fun review(activity: Activity)

    ///////////////////////////////////////////////////////////////////////////
    // Misc
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Events emitted through [ReviewManager.event].
     */
    enum class ReviewEvent {

        /**
         * Every condition passed to [ReviewManager.monitor] is met, so the user can be asked for a review now.
         */
        Eligible;
    }

    private object Default {
        const val INSTALL_DAYS = 3
        const val LAUNCH_TIMES = 10
        const val REMIND_INTERVAL = 1
    }
}
