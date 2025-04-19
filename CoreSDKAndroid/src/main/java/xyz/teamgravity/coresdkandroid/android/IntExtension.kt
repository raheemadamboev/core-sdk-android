package xyz.teamgravity.coresdkandroid.android

fun Int?.orZero(): Int {
    return this ?: 0
}