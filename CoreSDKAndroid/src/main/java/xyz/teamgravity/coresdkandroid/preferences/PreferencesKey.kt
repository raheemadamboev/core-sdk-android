package xyz.teamgravity.coresdkandroid.preferences

interface PreferencesKey {
    val key: String
    val default: Any?
    val encrypted: Boolean
}