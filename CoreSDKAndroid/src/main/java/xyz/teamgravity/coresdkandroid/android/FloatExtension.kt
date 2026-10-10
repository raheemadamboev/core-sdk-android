package xyz.teamgravity.coresdkandroid.android

fun Float?.orZero(): Float {
    return this ?: 0F
}