package xyz.teamgravity.coresdkandroid.notification

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.graphics.BitmapFactory
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.Action
import xyz.teamgravity.coresdkandroid.android.BuildUtil

class NotificationManager(
    private val application: Application,
    private val manager: NotificationManager
) {

    ///////////////////////////////////////////////////////////////////////////
    // API
    ///////////////////////////////////////////////////////////////////////////

    fun canPostNotification(): Boolean {
        if (BuildUtil.atLeast33()) return manager.areNotificationsEnabled()
        return true
    }

    fun createChannel(
        id: String,
        @StringRes name: Int,
        @StringRes description: Int,
        importance: Int = NotificationManager.IMPORTANCE_DEFAULT,
        vibrate: Boolean = true,
        showBadge: Boolean = true
    ) {
        if (canPostNotification()) {
            val channel = NotificationChannel(
                id,
                application.getString(name),
                importance
            )
            channel.description = application.getString(description)
            channel.enableVibration(vibrate)
            channel.setShowBadge(showBadge)
            manager.createNotificationChannel(channel)
        }
    }

    fun createNotification(
        channelId: String,
        contentIntent: PendingIntent? = null,
        @DrawableRes smallIcon: Int,
        @DrawableRes largeIcon: Int,
        title: String? = null,
        message: String,
        visibility: Int = NotificationCompat.VISIBILITY_PUBLIC,
        autoCancel: Boolean = true,
        priority: Int = NotificationCompat.PRIORITY_DEFAULT,
        action: Action? = null
    ): Notification {
        val builder = NotificationCompat.Builder(application, channelId).apply {
            if (contentIntent != null) setContentIntent(contentIntent)
            setSmallIcon(smallIcon)
            setLargeIcon(BitmapFactory.decodeResource(application.resources, largeIcon))
            if (title != null) setContentTitle(title)
            setContentText(message)
            setVisibility(visibility)
            setAutoCancel(autoCancel)
            setPriority(priority)
            if (action != null) addAction(action)
        }

        val style = NotificationCompat.BigTextStyle(builder)
        if (title != null) style.setBigContentTitle(title)
        style.bigText(message)
        return style.build() ?: builder.build()
    }

    fun show(
        id: Int,
        notification: Notification
    ) {
        if (canPostNotification()) {
            manager.notify(id, notification)
        }
    }

    fun hide(id: Int) {
        manager.cancel(id)
    }

    fun hideAll() {
        manager.cancelAll()
    }
}