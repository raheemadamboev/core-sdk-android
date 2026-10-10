package xyz.teamgravity.coresdkandroid.android

fun Long?.orZero(): Long {
    return this ?: 0L
}