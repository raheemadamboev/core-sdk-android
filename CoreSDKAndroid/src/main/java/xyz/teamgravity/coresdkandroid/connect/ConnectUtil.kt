package xyz.teamgravity.coresdkandroid.connect

import android.content.Context
import android.content.Intent
import androidx.core.app.ShareCompat
import androidx.core.net.toUri
import xyz.teamgravity.coresdkandroid.android.safelyStartActivity

object ConnectUtil {

    private const val GRAVITY = "dev?id=5173702047357476752"

    ///////////////////////////////////////////////////////////////////////////
    // API
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Returns Play Store page url for the app.
     *
     * @param context
     * Context.
     * @param packageName
     * Package name of the app. Current app's package name is provided by default.
     *
     */
    fun getAppPlayStorePageUrl(
        context: Context,
        packageName: String = context.packageName
    ): String {
        return "https://play.google.com/store/apps/details?id=$packageName"
    }

    /**
     * Shares text with other apps.
     *
     * @param context
     * Context.
     * @param text
     * Text that is shared.
     * @param chooserTitle
     * Title for the chooser modal.
     */
    fun shareText(
        context: Context,
        text: String,
        chooserTitle: String
    ) {
        ShareCompat.IntentBuilder(context)
            .setType("text/plain")
            .setText(text)
            .setChooserTitle(chooserTitle)
            .startChooser()
    }

    /**
     * Navigates the user to connect us via Email.
     *
     * @param context
     * Context.
     * @param email
     * Email to sent.
     * @param subject
     * Subject of the email.
     * @param chooserTitle
     * Title for the chooser modal.
     */
    fun connectViaEmail(
        context: Context,
        email: String,
        subject: String,
        chooserTitle: String
    ) {
        ShareCompat.IntentBuilder(context)
            .setType("message/rfc822")
            .addEmailTo(email)
            .setSubject(subject)
            .setChooserTitle(chooserTitle)
            .startChooser()
    }

    /**
     * Navigates the user to app Play Store page. First, it tries to navigate user to app page in Play Store app. If Play Store app doesn't exist
     * in user's device, it tries to navigate to app's page in web app if it exists in user's device.
     *
     * @param context
     * Context.
     * @param packageName
     * Package name of the app. Current app's package name is provided by default.
     */
    fun viewAppPlayStorePage(
        context: Context,
        packageName: String = context.packageName
    ) {
        context.safelyStartActivity(
            intent = Intent(Intent.ACTION_VIEW, "market://details?id=$packageName".toUri()),
            onError = {
                context.safelyStartActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        getAppPlayStorePageUrl(
                            context = context,
                            packageName = packageName
                        ).toUri()
                    )
                )
            }
        )
    }

    /**
     * Navigates the user to Gravity Play Store page. First, it tries to navigate user to Gravity page in Play Store app. If Play Store app doesn't exist
     * in user's device, it tries to navigate to Gravity page in web app if it exists in user's device.
     *
     * @param context
     * Context.
     */
    fun viewGravityPlayStorePage(context: Context) {
        context.safelyStartActivity(
            intent = Intent(Intent.ACTION_VIEW, "market://$GRAVITY".toUri()),
            onError = {
                context.safelyStartActivity(
                    Intent(Intent.ACTION_VIEW, "https://play.google.com/store/apps/$GRAVITY".toUri())
                )
            }
        )
    }
}