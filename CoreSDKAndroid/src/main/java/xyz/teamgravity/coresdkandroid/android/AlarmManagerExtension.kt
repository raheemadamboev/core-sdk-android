package xyz.teamgravity.coresdkandroid.android

import android.app.AlarmManager

fun AlarmManager.canSetExactAlarms(): Boolean {
    if (BuildUtil.atLeast31()) return canScheduleExactAlarms()
    return true
}