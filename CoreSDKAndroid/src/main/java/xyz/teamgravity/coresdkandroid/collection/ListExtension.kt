package xyz.teamgravity.coresdkandroid.collection

fun <T> List<T>.equalsIgnoreOrder(other: List<T>): Boolean {
    return size == other.size && toSet() == other.toSet()
}