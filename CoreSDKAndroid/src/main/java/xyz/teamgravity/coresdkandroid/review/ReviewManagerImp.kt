package xyz.teamgravity.coresdkandroid.review

import android.app.Activity
import android.content.Context
import com.google.android.play.core.review.ReviewManagerFactory
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import timber.log.Timber
import xyz.teamgravity.coresdkandroid.android.orZero
import xyz.teamgravity.coresdkandroid.connect.ConnectUtil
import xyz.teamgravity.coresdkandroid.coroutine.dispatcher.DispatcherProvider
import xyz.teamgravity.coresdkandroid.preferences.Preferences
import xyz.teamgravity.coresdkandroid.time.TimeUtil
import java.time.LocalDate
import java.time.temporal.ChronoUnit

private typealias PlayReviewManager = com.google.android.play.core.review.ReviewManager

/**
 * Keeps the date of the first [monitor] call, the launch count, the remind-later date and whether to keep asking in
 * [preferences], and shows the review through the Play In-App Review API. When the review flow fails, it opens the
 * app's Play Store page instead. After [review] or [deny], it never asks again.
 *
 * @property preferences
 * Storage for the review state.
 * @property dispatcher
 * Dispatchers for storage and Play review requests.
 * @param context
 * Context used to create the Play review client.
 */
class ReviewManagerImp(
    private val preferences: Preferences,
    private val dispatcher: DispatcherProvider,
    context: Context
) : ReviewManager {

    private val manager: PlayReviewManager = ReviewManagerFactory.create(context)
    private val scope: CoroutineScope = CoroutineScope(dispatcher.io + SupervisorJob())

    private val _event = Channel<ReviewManager.ReviewEvent>()
    override val event: Flow<ReviewManager.ReviewEvent> = _event.receiveAsFlow()

    ///////////////////////////////////////////////////////////////////////////
    // API
    ///////////////////////////////////////////////////////////////////////////

    override fun monitor(
        installDays: Int,
        launchTimes: Int,
        remindInterval: Int
    ) {
        scope.launch {
            val shouldCheck = preferences.getBoolean(ReviewPreferences.ShouldCheck).first()
            if (shouldCheck == null) {
                Timber.e("monitor(): shouldCheck is null! Aborted the operation.")
                return@launch
            }

            if (!shouldCheck) {
                Timber.i("monitor(): shouldCheck is false. Aborted the operation.")
                return@launch
            }

            val installDate = run {
                val value = preferences.getLong(ReviewPreferences.InstallDate).first()
                if (value == null) {
                    Timber.d("monitor(): installDate is null. Put current date.")
                    preferences.upsertLong(ReviewPreferences.InstallDate, System.currentTimeMillis())
                }
                value?.let(TimeUtil::fromLongToLocalDate) ?: LocalDate.now()
            }

            val currentLaunchTimes = preferences.getInt(ReviewPreferences.LaunchTimes).first().orZero() + 1
            preferences.upsertInt(ReviewPreferences.LaunchTimes, currentLaunchTimes)
            if (currentLaunchTimes >= launchTimes) {
                val days = ChronoUnit.DAYS.between(installDate, LocalDate.now())
                if (days >= installDays) {
                    val intervalDate = preferences.getLong(ReviewPreferences.IntervalDate).first()?.let(TimeUtil::fromLongToLocalDate)
                    if (intervalDate == null) {
                        _event.send(ReviewManager.ReviewEvent.Eligible)
                    } else {
                        val intervalDays = ChronoUnit.DAYS.between(intervalDate, LocalDate.now())
                        if (intervalDays >= remindInterval) {
                            _event.send(ReviewManager.ReviewEvent.Eligible)
                        }
                    }
                }
            }
        }
    }

    override fun remindLater() {
        scope.launch {
            preferences.upsertLong(ReviewPreferences.IntervalDate, System.currentTimeMillis())
        }
    }

    override fun deny() {
        scope.launch {
            preferences.upsertBoolean(ReviewPreferences.ShouldCheck, false)
        }
    }

    override fun review(activity: Activity) {
        scope.launch {
            preferences.upsertBoolean(ReviewPreferences.ShouldCheck, false)
            try {
                withContext(dispatcher.io) {
                    val request = manager.requestReviewFlow().await()
                    manager.launchReviewFlow(activity, request).await()
                }
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                Timber.e(e)
                ConnectUtil.viewAppPlayStorePage(activity)
            }
        }
    }
}
