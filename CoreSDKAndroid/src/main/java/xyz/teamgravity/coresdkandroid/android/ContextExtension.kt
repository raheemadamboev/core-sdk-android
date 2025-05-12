package xyz.teamgravity.coresdkandroid.android

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent

/**
 * Safely starts activity. If activity is not found or not permitted, calls [onError] lambda.
 *
 * @param intent
 * Intent to start.
 * @param onError
 * Lambda that is called when starting activity is not found or not permitted.
 */
fun Context.safelyStartActivity(
    intent: Intent,
    onError: () -> Unit = {}
) {
    try {
        startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        onError()
    } catch (e: SecurityException) {
        onError()
    }
}