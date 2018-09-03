package com.way2bay.runtimepermissions

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import org.jetbrains.annotations.NotNull
import java.util.*

class PermissionManager {
    private val mPermissionsNeeded = ArrayList<String>()
    private val mPermissionsDenied = ArrayList<String>()
    private var mActivity: Activity
    private var mOnPermissionResultListener: OnPermissionResultListener? = null
    private var mPermissionBlocked: Boolean = false

    constructor(activity: Activity) {
        this.mActivity = activity

        try {
            val pm = mActivity.applicationContext.packageManager
            val pi = pm.getPackageInfo(mActivity.applicationContext.packageName, PackageManager.GET_PERMISSIONS)
            val permissionInfo = pi.requestedPermissions
            Collections.addAll(mPermissionsNeeded, *permissionInfo)
        } catch (ignored: Exception) {

        }

    }

    constructor(activity: Activity,permissions: Array<String>) {
        this.mActivity = activity

        Collections.addAll(mPermissionsNeeded, *permissions)
    }

    fun setPermissionListener(onPermissionResultListener: OnPermissionResultListener) {
        mOnPermissionResultListener = onPermissionResultListener
    }

    fun checkAndRequestPermissions(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val listPermissionsAssign = ArrayList<String>()
            for (per in mPermissionsNeeded) {
                if (ContextCompat.checkSelfPermission(mActivity, per) != PackageManager.PERMISSION_GRANTED) {
                    listPermissionsAssign.add(per)
                }
            }

            if (!listPermissionsAssign.isEmpty()) {
                ActivityCompat.requestPermissions(mActivity, listPermissionsAssign.toTypedArray<String>(), PERMISSION_REQUEST)
                return false
            }
        }
        onPermissionGranted()
        return true
    }

    fun checkBlockedPermissions(): Boolean {
        if (mPermissionBlocked) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val listPermissionsAssign = ArrayList<String>()
                for (per in mPermissionsNeeded) {
                    if (ContextCompat.checkSelfPermission(mActivity, per) != PackageManager.PERMISSION_GRANTED) {
                        listPermissionsAssign.add(per)
                    }
                }

                if (!listPermissionsAssign.isEmpty()) {
                    onPermissionBlocked()
                    return false
                } else {
                    onPermissionGranted()
                    return true
                }
            } else {
                return true
            }
        }
        return true
    }

    fun checkPermissionResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST -> {
                val perms = HashMap<String, Int>()
                for (permission in mPermissionsNeeded) {
                    perms[permission] = PackageManager.PERMISSION_GRANTED
                }
                if (grantResults.size > 0) {
                    for (i in permissions.indices)
                        perms[permissions[i]] = grantResults[i]
                    var isAllGranted = true
                    for (permission in mPermissionsNeeded) {
                        if (perms[permission] == PackageManager.PERMISSION_DENIED) {
                            isAllGranted = false
                            mPermissionsDenied.add(permission)
                        }
                    }
                    if (isAllGranted) {
                        onPermissionGranted()

                    } else {
                        var shouldRequest = false
                        for (permission in mPermissionsNeeded) {
                            if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity, permission)) {
                                shouldRequest = true
                                break
                            }
                        }
                        if (shouldRequest) {
                            onPermissionDenied()
                        } else {
                            onPermissionBlocked()
                        }
                    }
                }
            }
        }
    }

    private fun onPermissionDenied() {
        if (mOnPermissionResultListener != null)
            mOnPermissionResultListener!!.onPermissionDenied(mPermissionsDenied)
    }

    private fun onPermissionBlocked() {
        if (mOnPermissionResultListener != null)
            mOnPermissionResultListener!!.onPermissionBlocked(mPermissionsDenied)
        mPermissionBlocked = true
    }

    private fun onPermissionGranted() {
        if (mOnPermissionResultListener != null)
            mOnPermissionResultListener!!.onPermissionGranted()

        mPermissionBlocked = false
    }

    companion object {

        private val PERMISSION_REQUEST = 10
    }
}