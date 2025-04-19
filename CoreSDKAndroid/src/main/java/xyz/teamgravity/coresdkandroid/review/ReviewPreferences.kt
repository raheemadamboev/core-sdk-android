package xyz.teamgravity.coresdkandroid.review

import xyz.teamgravity.coresdkandroid.preferences.PreferencesKey

internal enum class ReviewPreferences(
    override val key: String,
    override val default: Any?,
    override val encrypted: Boolean
) : PreferencesKey {
    InstallDate(
        key = "install_date",
        default = null,
        encrypted = false
    ),
    IntervalDate(
        key = "interval_date",
        default = null,
        encrypted = false
    ),
    LaunchTimes(
        key = "launch_times",
        default = 0,
        encrypted = false
    ),
    ShouldCheck(
        key = "should_check",
        default = true,
        encrypted = false
    );
}