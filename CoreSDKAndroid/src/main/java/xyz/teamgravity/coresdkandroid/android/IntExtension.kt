package xyz.teamgravity.coresdkandroid.android

/**
 * Returns the int value if the value is not null. Otherwise, returns 0.
 */
fun Int?.orZero(): Int {
    return this ?: 0
}