package xyz.teamgravity.coresdkandroid.review

import xyz.teamgravity.coresdkandroid.preferences.PreferencesKey

internal enum class ReviewPreferences(
    override val key: String,
    override val default: Any?,
    override val encrypted: Boolean
) : PreferencesKey {
    InstallDate(
        key = "xyz.teamgravity.coresdkandroid.review.InstallDate",
        default = null,
        encrypted = false
    ),
    IntervalDate(
        key = "xyz.teamgravity.coresdkandroid.review.IntervalDate",
        default = null,
        encrypted = false
    ),
    LaunchTimes(
        key = "xyz.teamgravity.coresdkandroid.review.LaunchTimes",
        default = 0,
        encrypted = false
    ),
    ShouldCheck(
        key = "xyz.teamgravity.coresdkandroid.review.ShouldCheck",
        default = true,
        encrypted = false
    );
}