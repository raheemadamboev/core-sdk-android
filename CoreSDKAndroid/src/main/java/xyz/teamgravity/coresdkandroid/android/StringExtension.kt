package xyz.teamgravity.coresdkandroid.android

import java.math.BigDecimal
import java.util.Locale

fun String.capitalize(): String {
    return replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
}

fun String?.toFloatOrZero(): Float {
    return this?.toFloatOrNull().orZero()
}

fun String?.removeTrailingZeros(): String {
    if (this == null) return ""
    if (!matches("[-+]?\\d*\\.?\\d+".toRegex())) return this
    return BigDecimal(this).stripTrailingZeros().toPlainString()
}

inline fun String?.ifNullOrBlank(defaultValue: () -> String?): String? {
    return if (isNullOrBlank()) defaultValue() else this
}

fun String.extension(): String? {
    val extension = substringAfterLast(
        delimiter = ".",
        missingDelimiterValue = ""
    ).trim()
    if (extension.isEmpty()) return null
    return extension
}

fun String.removeExtension(): String {
    val extension = extension() ?: return this
    val value = trim()
    return value.substring(0, value.length - (extension.length + 1))
}

fun String?.isJsonExtension(): Boolean {
    if (this == null) return false
    return extension()?.equals("json", true) ?: false
}

fun String?.isPdfExtension(): Boolean {
    if (this == null) return false
    return extension()?.equals("pdf", true) ?: false
}