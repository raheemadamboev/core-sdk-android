package xyz.teamgravity.coresdkandroid.coroutine

import kotlinx.coroutines.CancellableContinuation
import kotlin.coroutines.resume

/**
 * Resumes the coroutine if it is active.
 *
 * @param result
 * Result of coroutine.
 */
fun <T> CancellableContinuation<T?>.safelyResume(result: T?) {
    if (isActive) resume(result)
}

/**
 * Resumes the coroutine if it is active.
 *
 * @param result
 * Result of coroutine.
 */
fun CancellableContinuation<Boolean>.safelyResume(result: Boolean) {
    if (isActive) resume(result)
}

/**
 * Resumes the coroutine if it is active.
 */
fun CancellableContinuation<Unit>.safelyResume() {
    if (isActive) resume(Unit)
}