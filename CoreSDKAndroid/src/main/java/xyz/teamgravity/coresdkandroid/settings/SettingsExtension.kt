package xyz.teamgravity.coresdkandroid.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import xyz.teamgravity.coresdkandroid.android.safelyStartActivity

/**
 * Navigates the user to the app locale settings activity if it exists. Otherwise, does nothing.
 */
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
fun Context.navigateAppLocaleSettings() {
    val intent = Intent(Settings.ACTION_APP_LOCALE_SETTINGS)
    intent.data = Uri.fromParts("package", packageName, null)
    safelyStartActivity(intent)
}

/**
 * Navigates the user to the wireless settings activity if it exists. Otherwise, does nothing.
 */
fun Context.navigateWirelessSettings() {
    val intent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
    safelyStartActivity(intent)
}