package xyz.teamgravity.coresdkandroid.update

import android.content.Context
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import timber.log.Timber
import xyz.teamgravity.coresdkandroid.coroutine.dispatcher.DispatcherProvider

/**
 * Uses the Play In-App Updates API. An update whose priority, set through the Play Developer API when the release is
 * published, is 4 or 5 is [UpdateManager.Type.Forced]; a lower one is [UpdateManager.Type.Optional]. Each instance
 * announces an optional update and a finished download once, while a forced update is announced on every [monitor]
 * call.
 *
 * @property dispatcher
 * Dispatchers for Play update requests.
 * @param context
 * Context used to create the Play update client.
 */
class UpdateManagerImp(
    private val dispatcher: DispatcherProvider,
    context: Context
) : UpdateManager {

    private val manager: AppUpdateManager = AppUpdateManagerFactory.create(context)
    private val scope: CoroutineScope = CoroutineScope(dispatcher.io + SupervisorJob())

    private val _event = Channel<UpdateManager.UpdateEvent>()
    override val event: Flow<UpdateManager.UpdateEvent> = _event.receiveAsFlow()

    private var updateAvailableNotified: Boolean = false
    private var updateDownloadedNotified: Boolean = false

    private val listener = InstallStateUpdatedListener { state ->
        if (!updateDownloadedNotified && state.installStatus() == InstallStatus.DOWNLOADED) {
            _event.trySend(UpdateManager.UpdateEvent.Downloaded)
            updateDownloadedNotified = true
        }
    }

    private suspend fun getAppUpdateInfoSafely(): AppUpdateInfo? {
        return withContext(dispatcher.io) {
            try {
                return@withContext manager.appUpdateInfo.await()
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                Timber.e(e)
                return@withContext null
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // API
    ///////////////////////////////////////////////////////////////////////////

    override fun monitor() {
        scope.launch {
            val info = getAppUpdateInfoSafely() ?: return@launch
            if (!updateDownloadedNotified && info.installStatus() == InstallStatus.DOWNLOADED) {
                Timber.i("start(): update is already downloaded.")
                _event.trySend(UpdateManager.UpdateEvent.Downloaded)
                updateDownloadedNotified = true
                return@launch
            }

            val result = info.updateAvailability()

            if (result == UpdateAvailability.UPDATE_NOT_AVAILABLE || result == UpdateAvailability.UNKNOWN) {
                Timber.i("start(): update is not available.")
                return@launch
            }

            if (result == UpdateAvailability.UPDATE_AVAILABLE) {
                val type = UpdateManager.Type.fromPriority(info.updatePriority())
                if (type == UpdateManager.Type.Optional && updateAvailableNotified) return@launch
                _event.trySend(UpdateManager.UpdateEvent.Available(type))
                updateAvailableNotified = true
            } else {
                _event.trySend(UpdateManager.UpdateEvent.StartDownload)
            }

        }
    }

    override fun stop() {
        manager.unregisterListener(listener)
    }

    override suspend fun checkAppUpdate(): UpdateManager.Type {
        val info = getAppUpdateInfoSafely() ?: return UpdateManager.Type.None
        if (info.updateAvailability() != UpdateAvailability.UPDATE_AVAILABLE) return UpdateManager.Type.None
        return UpdateManager.Type.fromPriority(info.updatePriority())
    }

    override fun downloadAppUpdate(launcher: ActivityResultLauncher<IntentSenderRequest>) {
        scope.launch {
            val info = getAppUpdateInfoSafely() ?: return@launch
            val result = info.updateAvailability()
            if (result == UpdateAvailability.UPDATE_NOT_AVAILABLE || result == UpdateAvailability.UNKNOWN) {
                Timber.w("downloadAppUpdate(): update is not available. Aborted the operation.")
                return@launch
            }

            val type = UpdateManager.Type.fromPriority(info.updatePriority())
            Timber.i("downloadAppUpdate(): starting $type update.")

            manager.registerListener(listener)

            manager.startUpdateFlowForResult(
                info,
                launcher,
                AppUpdateOptions.defaultOptions(type.value)
            )
        }
    }

    override fun installAppUpdate() {
        manager.completeUpdate()
    }
}
