package com.multistoryparking.app.utils

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.View
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.multistoryparking.app.BuildConfig

private val basicPermissions = arrayOf(
    Manifest.permission.CAMERA,
    Manifest.permission.READ_EXTERNAL_STORAGE,
    Manifest.permission.WRITE_EXTERNAL_STORAGE
)

const val PERMISSION_REQUEST = 0

@RequiresApi(Build.VERSION_CODES.M)
fun Fragment.withPermission(
    permissions: Array<String> = basicPermissions,
    func: () -> Unit
) = when (PackageManager.PERMISSION_GRANTED) {
    context?.checkSelfPermission(permissions.first()) -> func()
    else -> requestPermissions(permissions, PERMISSION_REQUEST)
}