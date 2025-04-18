package xyz.teamgravity.coresdkandroid.update

import android.content.Context
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallException
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import timber.log.Timber

class UpdateManager(
    context: Context
) {

    private val manager: AppUpdateManager = AppUpdateManagerFactory.create(context)
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val _event = Channel<UpdateEvent>()
    val event: Flow<UpdateEvent> = _event.receiveAsFlow()

    private val listener = InstallStateUpdatedListener { state ->
        if (state.installStatus() == InstallStatus.DOWNLOADED) {
            _event.trySend(UpdateEvent.Downloaded)
        }
    }

    private suspend fun getAppUpdateInfoSafely(): AppUpdateInfo? {
        return withContext(Dispatchers.IO) {
            try {
                return@withContext manager.appUpdateInfo.await()
            } catch (e: InstallException) {
                Timber.e(e)
                return@withContext null
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // API
    ///////////////////////////////////////////////////////////////////////////

    fun start() {
        scope.launch {
            val info = getAppUpdateInfoSafely() ?: return@launch
            if (info.installStatus() == InstallStatus.DOWNLOADED) {
                Timber.i("start(): update is already downloaded.")
                _event.trySend(UpdateEvent.Downloaded)
                return@launch
            }

            val result = info.updateAvailability()

            if (result == UpdateAvailability.UPDATE_NOT_AVAILABLE || result == UpdateAvailability.UNKNOWN) {
                Timber.i("start(): update is not available.")
                return@launch
            }

            _event.trySend(
                UpdateEvent.Available(
                    type = Type.fromPriority(info.updatePriority()),
                    inform = result == UpdateAvailability.UPDATE_AVAILABLE
                )
            )
        }
    }

    fun stop() {
        manager.unregisterListener(listener)
    }

    suspend fun checkAppUpdate(): Type {
        val info = getAppUpdateInfoSafely() ?: return Type.None
        if (info.updateAvailability() != UpdateAvailability.UPDATE_AVAILABLE) return Type.None
        return Type.fromPriority(info.updatePriority())
    }

    fun downloadAppUpdate(launcher: ActivityResultLauncher<IntentSenderRequest>) {
        scope.launch {
            val info = getAppUpdateInfoSafely() ?: return@launch
            val result = info.updateAvailability()
            if (result == UpdateAvailability.UPDATE_NOT_AVAILABLE || result == UpdateAvailability.UNKNOWN) {
                Timber.w("downloadAppUpdate(): update is not available. Aborted the operation.")
                return@launch
            }

            val type = Type.fromPriority(info.updatePriority())
            Timber.i("downloadAppUpdate(): starting $type update.")

            manager.registerListener(listener)

            manager.startUpdateFlowForResult(
                info,
                launcher,
                AppUpdateOptions.defaultOptions(type.value)
            )
        }
    }

    fun installAppUpdate() {
        manager.completeUpdate()
    }

    ///////////////////////////////////////////////////////////////////////////
    // MISC
    ///////////////////////////////////////////////////////////////////////////

    enum class Type(
        val value: Int
    ) {
        None(-1),
        Optional(AppUpdateType.FLEXIBLE),
        Forced(AppUpdateType.IMMEDIATE);

        companion object {
            fun fromPriority(priority: Int): Type {
                return if (priority > 3) Forced else Optional
            }
        }
    }

    sealed interface UpdateEvent {
        data class Available(
            val type: Type,
            val inform: Boolean
        ) : UpdateEvent

        data object Downloaded : UpdateEvent
    }
}