package xyz.teamgravity.coresdkandroid.review

import android.app.Activity
import android.content.Context
import com.google.android.play.core.review.ReviewException
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
import xyz.teamgravity.coresdkandroid.preferences.Preferences
import xyz.teamgravity.coresdkandroid.time.TimeUtil
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class ReviewManager(
    private val preferences: Preferences,
    context: Context
) {

    private val manager: ReviewManager = ReviewManagerFactory.create(context)
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val _event = Channel<ReviewEvent>()
    val event: Flow<ReviewEvent> = _event.receiveAsFlow()

    ///////////////////////////////////////////////////////////////////////////
    // API
    ///////////////////////////////////////////////////////////////////////////

    fun monitor(
        installDays: Int = Default.INSTALL_DAYS,
        launchTimes: Int = Default.LAUNCH_TIMES,
        remindInterval: Int = Default.REMIND_INTERVAL
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

            val currentLaunchTimes = preferences.getInt(ReviewPreferences.LaunchTimes).first().orZero() + 1
            preferences.upsertInt(ReviewPreferences.LaunchTimes, currentLaunchTimes)
            if (currentLaunchTimes >= launchTimes) {
                val installDate = run {
                    val value = preferences.getLong(ReviewPreferences.InstallDate).first()
                    if (value == null) {
                        Timber.d("monitor(): installDate is null. Put current date.")
                        preferences.upsertLong(ReviewPreferences.InstallDate, System.currentTimeMillis())
                    }
                    value?.let(TimeUtil::fromLongToLocalDate) ?: LocalDate.now()
                }

                val days = ChronoUnit.DAYS.between(installDate, LocalDate.now())
                if (days >= installDays) {
                    val intervalDate = preferences.getLong(ReviewPreferences.IntervalDate).first()?.let(TimeUtil::fromLongToLocalDate)
                    if (intervalDate == null) {
                        _event.send(ReviewEvent.Eligible)
                    } else {
                        val intervalDays = ChronoUnit.DAYS.between(intervalDate, LocalDate.now())
                        if (intervalDays >= remindInterval) {
                            _event.send(ReviewEvent.Eligible)
                        }
                    }
                }
            }
        }
    }

    fun remindLater() {
        scope.launch {
            preferences.upsertLong(ReviewPreferences.IntervalDate, System.currentTimeMillis())
        }
    }

    fun deny() {
        scope.launch {
            preferences.upsertBoolean(ReviewPreferences.ShouldCheck, false)
        }
    }

    fun review(activity: Activity) {
        scope.launch {
            preferences.upsertBoolean(ReviewPreferences.ShouldCheck, false)
            try {
                withContext(Dispatchers.IO) {
                    val request = manager.requestReviewFlow().await()
                    manager.launchReviewFlow(activity, request)
                }
            } catch (e: ReviewException) {
                Timber.e(e)
                ConnectUtil.viewAppPlayStorePage(activity)
            }
        }
    }


    ///////////////////////////////////////////////////////////////////////////
    // Misc
    ///////////////////////////////////////////////////////////////////////////

    enum class ReviewEvent {
        Eligible;
    }

    private object Default {
        const val INSTALL_DAYS = 3
        const val LAUNCH_TIMES = 10
        const val REMIND_INTERVAL = 1
    }
}