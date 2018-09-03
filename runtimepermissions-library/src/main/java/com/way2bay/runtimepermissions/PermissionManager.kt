package com.way2bay.runtimepermissions

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.util.Log
import java.util.*

class PermissionManager {
    private val mPermissionsNeeded = ArrayList<String>()
    private val mPermissionsDenied = ArrayList<String>()
    private var mActivity: Activity
    private var mOnPermissionResultListener: OnPermissionResultListener? = null
    private var mPermissionBlocked: Boolean = false
    private var mOnPermissionsGrantedFunction: (() -> Unit)? = null
    private var mOnPermissionsDeniedFunction: (() -> Unit)? = null
    private var mOnPermissionsBlockedFunction: (() -> Unit)? = null
    private var enableLogs: Boolean = false
    private val TAG: String = "Runtime Permission"

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

    constructor(activity: Activity, permissions: Array<String>) {
        this.mActivity = activity

        Collections.addAll(mPermissionsNeeded, *permissions)
    }

    fun enableLogs(enableLogs: Boolean) {
        this@PermissionManager.enableLogs = enableLogs
    }

    fun executeOnPermissionGranted(func: () -> Unit) {
        this@PermissionManager.mOnPermissionsGrantedFunction = func
    }


    fun executeOnPermissionDenied(func: () -> Unit) {
        this@PermissionManager.mOnPermissionsDeniedFunction = func
    }


    fun executeOnPermissionBlocked(func: () -> Unit) {
        this@PermissionManager.mOnPermissionsBlockedFunction = func
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

        if (enableLogs)
            Log.d(TAG, "onPermissionDenied")

        if (mOnPermissionResultListener == null && mOnPermissionsDeniedFunction == null)
            AlertDialog.Builder(mActivity)
                    .setMessage(R.string.permission_required)
                    .setPositiveButton(R.string.grant, DialogInterface.OnClickListener { dialogInterface, i -> checkAndRequestPermissions() })
                    .setNegativeButton(android.R.string.cancel, null)
                    .setCancelable(false)
                    .show()
        else if (mOnPermissionResultListener != null)
            mOnPermissionResultListener!!.onPermissionDenied(mPermissionsDenied)
        else
            mOnPermissionsDeniedFunction?.invoke()
    }

    private fun onPermissionBlocked() {
        if (enableLogs)
            Log.d(TAG, "onPermissionBlocked")

        if (mOnPermissionResultListener == null && mOnPermissionsBlockedFunction == null)
            AlertDialog.Builder(mActivity)
                    .setMessage(R.string.permission_blocked)
                    .setPositiveButton(R.string.grant, DialogInterface.OnClickListener { dialogInterface, i ->
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", mActivity.packageName, null))
                        mActivity.startActivityForResult(intent, PermissionManager.REQUEST_PERMISSION_SETTINGS)

                    })
                    .setNegativeButton(android.R.string.cancel, null)
                    .setCancelable(false)
                    .show()
        else if (mOnPermissionResultListener != null)
            mOnPermissionResultListener!!.onPermissionBlocked(mPermissionsDenied)
        else
            mOnPermissionsBlockedFunction?.invoke()

        mPermissionBlocked = true
    }

    private fun onPermissionGranted() {

        if (enableLogs)
            Log.d(TAG, "onPermissionGranted")

        if (mOnPermissionResultListener != null)
            mOnPermissionResultListener!!.onPermissionGranted()
        else
            mOnPermissionsGrantedFunction?.invoke()

        if (mOnPermissionResultListener == null && mOnPermissionsGrantedFunction == null)
            Log.e(TAG, "onPermissionGranted method is not provided or implemented.")

        mPermissionBlocked = false


    }

    companion object {
        val REQUEST_PERMISSION_SETTINGS = 20

        private val PERMISSION_REQUEST = 10
    }
}