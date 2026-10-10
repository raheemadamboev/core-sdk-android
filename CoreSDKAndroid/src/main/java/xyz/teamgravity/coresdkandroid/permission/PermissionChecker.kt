package xyz.teamgravity.coresdkandroid.permission

interface PermissionChecker {

    /**
     * Returns true if [permission] is granted, otherwise false.
     */
    fun isGranted(permission: String): Boolean

    /**
     * Returns true if all [permissions] are granted, otherwise false.
     */
    fun isGrantedAll(permissions: Array<String>): Boolean
}