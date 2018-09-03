package com.way2bay.runtimepermissions

import java.util.*

interface OnPermissionResultListener {
    fun onPermissionGranted()

    fun onPermissionDenied(permissions: ArrayList<String>) {}

    fun onPermissionBlocked(permissions: ArrayList<String>)
}