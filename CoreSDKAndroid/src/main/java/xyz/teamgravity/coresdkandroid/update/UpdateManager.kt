package xyz.teamgravity.coresdkandroid.update

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.google.android.play.core.install.model.AppUpdateType
import kotlinx.coroutines.flow.Flow

/**
 * Checks Google Play for a new version of the app and runs the in-app update flow.
 */
interface UpdateManager {

    /**
     * Emits [UpdateEvent]s while an update is found, started and downloaded.
     */
    val event: Flow<UpdateEvent>

    ///////////////////////////////////////////////////////////////////////////
    // API
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Checks for an update and reports what it finds through [event].
     */
    fun monitor()

    /**
     * Stops listening to download progress.
     */
    fun stop()

    /**
     * Checks once whether an update is available.
     *
     * @return Type of the available update, or [Type.None] when there is none.
     */
    suspend fun checkAppUpdate(): Type

    /**
     * Starts downloading the available update.
     *
     * @param launcher
     * Launcher that starts the Play update flow.
     */
    fun downloadAppUpdate(launcher: ActivityResultLauncher<IntentSenderRequest>)

    /**
     * Installs the downloaded update and restarts the app.
     */
    fun installAppUpdate()

    ///////////////////////////////////////////////////////////////////////////
    // Misc
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Kind of update Google Play offers.
     *
     * @property value
     * Play's AppUpdateType for this kind, or -1 for [None].
     */
    enum class Type(
        val value: Int
    ) {

        /**
         * No update is available.
         */
        None(-1),

        /**
         * An update the user may install later; it downloads in the background while the app keeps running.
         */
        Optional(AppUpdateType.FLEXIBLE),

        /**
         * An update the user must install before using the app.
         */
        Forced(AppUpdateType.IMMEDIATE);

        companion object {

            /**
             * Maps the priority of a Play update to its kind.
             *
             * @param priority
             * Update priority from 0 to 5, set for the release on Google Play.
             * @return [Forced] for priority 4 or 5, [Optional] otherwise.
             */
            fun fromPriority(priority: Int): Type {
                return if (priority > 3) Forced else Optional
            }
        }
    }

    /**
     * Events emitted through [UpdateManager.event].
     */
    sealed interface UpdateEvent {

        /**
         * An update is available.
         *
         * @property type
         * Kind of the available update.
         */
        data class Available(val type: Type) : UpdateEvent

        /**
         * Asks the app to start the download with [UpdateManager.downloadAppUpdate] right away, without asking the user
         * again: an update the user already accepted was interrupted and has to resume.
         */
        data object StartDownload : UpdateEvent

        /**
         * The update is downloaded and ready for [UpdateManager.installAppUpdate].
         */
        data object Downloaded : UpdateEvent
    }
}
