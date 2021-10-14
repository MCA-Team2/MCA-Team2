package com.jwpyo.soundmind.utils

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.jwpyo.soundmind.utils.Constant.REQUEST_PERMISSION_CODE

class PermissionManager(
    private val activity: Activity
) {
    private fun checkSinglePermissionGranted(permission: String): Boolean {
        val permissionResult = ContextCompat.checkSelfPermission(activity, permission)
        return permissionResult == PackageManager.PERMISSION_GRANTED
    }

    fun assertPermissionOrRequest(vararg permissions: String) {
        val isGranted = permissions.toList().all { checkSinglePermissionGranted(it) }
        if (!isGranted) {
            ActivityCompat.requestPermissions(activity, permissions, REQUEST_PERMISSION_CODE)
        }
    }
}