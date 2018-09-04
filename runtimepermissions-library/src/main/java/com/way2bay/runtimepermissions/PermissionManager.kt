package com.way2bay.runtimepermissions

import android.app.Activity
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

/**
 * <h1>PermissionManager</h1>
 * This class contains all the boilerplate code and is responsible to handle
 * logical conditions of runtime permission requests.
 *
 * Objective of this class is to provide hassle free and clean implementation
 * of Runtime permissions to Android developers.
 *
 * @author  Keshav Kumar Verma
 * @version 1.0
 * @since   2018-09-04
 */
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
    private val TAG: String = "Permission Manager"

    constructor(activity: Activity) {
        this.mActivity = activity
    }

    constructor(activity: Activity, permissions: Array<String>) {
        this.mActivity = activity
        Collections.addAll(mPermissionsNeeded, *permissions)
    }

    /**
     * This method is used to add permission.
     * @param permission permission in string.
     */
    fun addPermission(permission: String) {
        if (!mPermissionsNeeded.contains(permission))
            mPermissionsNeeded.add(permission)
    }

    /**
     * This method is used to remove permission.
     * @param permission permission in string.
     */
    fun removePermission(permission: String) {
        if (mPermissionsNeeded.contains(permission))
            mPermissionsNeeded.remove(permission)
    }

    /**
     * This method is used to set permissions.
     * @param permission permission in string array.
     */
    fun setPermissions(permissions: Array<String>) {
        mPermissionsNeeded.clear()
        Collections.addAll(mPermissionsNeeded, *permissions)
    }

    /**
     * This is used to enable or disable logs for permission manager.
     * By default logs are disabled.
     * @param enableLogs set true to enable logs.
     */
    fun enableLogs(enableLogs: Boolean) {
        this@PermissionManager.enableLogs = enableLogs
    }

    /**
     * This method is used to set function to be executed when permission granted.
     * @param func Function or block code is required.
     */
    fun executeOnPermissionGranted(func: () -> Unit) {
        this@PermissionManager.mOnPermissionsGrantedFunction = func
    }

    /**
     * This method is used to set function to be executed when permission denied.
     * @param func Function or block code is required.
     */
    fun executeOnPermissionDenied(func: () -> Unit) {
        this@PermissionManager.mOnPermissionsDeniedFunction = func
    }

    /**
     * This method is used to set function to be executed when permission blocked.
     * @param func Function or block code is required.
     */
    fun executeOnPermissionBlocked(func: () -> Unit) {
        this@PermissionManager.mOnPermissionsBlockedFunction = func
    }

    /**
     * Set permissionlistener to receive callbacks of events
     * @param onPermissionResultListener
     */
    fun setPermissionListener(onPermissionResultListener: OnPermissionResultListener) {
        mOnPermissionResultListener = onPermissionResultListener
    }

    /**
     * This method is used to check and request permissions.
     * @return true if permissions are granted.
     */
    fun checkAndRequestPermissions(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val listPermissionsAssign = ArrayList<String>()
            for (per in mPermissionsNeeded) {
                if (ContextCompat.checkSelfPermission(mActivity, per) != PackageManager.PERMISSION_GRANTED) {
                    listPermissionsAssign.add(per)
                }
            }

            if (!listPermissionsAssign.isEmpty()) {
                ActivityCompat.requestPermissions(mActivity, listPermissionsAssign.toTypedArray(), Companion.PERMISSION_REQUEST)
                return false
            }
        }
        onPermissionGranted()
        return true
    }

    /**
     * This method is used to check blocked permissions.
     * @return false if permissions are blocked.
     */
    fun checkBlockedPermissions(): Boolean {
        if (mPermissionBlocked) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val listPermissionsAssign = ArrayList<String>()
                for (per in mPermissionsNeeded) {
                    if (ContextCompat.checkSelfPermission(mActivity, per) != PackageManager.PERMISSION_GRANTED) {
                        listPermissionsAssign.add(per)
                    }
                }

                return if (!listPermissionsAssign.isEmpty()) {
                    onPermissionBlocked()
                    false
                } else {
                    onPermissionGranted()
                    true
                }
            } else {
                return true
            }
        }
        return true
    }

    /**
     * This method will internally check the result of permission request and invoke callbacks respectively.
     * @param requestCode Integer value of the request.
     * @param permissions array of permissions requested
     * @param grantResults array of granted permissions
     */
    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST -> {
                val perms = HashMap<String, Int>()
                for (permission in mPermissionsNeeded) {
                    perms[permission] = PackageManager.PERMISSION_GRANTED
                }
                if (grantResults.isNotEmpty()) {
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

    /**
     * This method will check the request code and call the checkAndRequestPermissions method.
     * @param requestCode Integer value of the request.
     */
    fun onActivityResult(requestCode: Int) {

        if (requestCode == PERMISSION_SETTINGS_REQUEST) {
            checkAndRequestPermissions()
        }
    }

    /**
     *  This method will be called internally when permissions are denied.
     */
    private fun onPermissionDenied() {

        if (enableLogs)
            Log.d(TAG, "onPermissionDenied")

        if (mOnPermissionResultListener == null && mOnPermissionsDeniedFunction == null)
            AlertDialog.Builder(mActivity)
                    .setMessage(R.string.permission_required)
                    .setPositiveButton(R.string.grant) { _, _ -> checkAndRequestPermissions() }
                    .setNegativeButton(android.R.string.cancel, null)
                    .setCancelable(false)
                    .show()
        else if (mOnPermissionResultListener != null)
            mOnPermissionResultListener!!.onPermissionDenied(mPermissionsDenied)
        else
            mOnPermissionsDeniedFunction?.invoke()
    }

    /**
     *  This method will be called internally when permissions are blocked.
     */
    private fun onPermissionBlocked() {
        if (enableLogs)
            Log.d(TAG, "onPermissionBlocked")

        if (mOnPermissionResultListener == null && mOnPermissionsBlockedFunction == null)
            AlertDialog.Builder(mActivity)
                    .setMessage(R.string.permission_blocked)
                    .setPositiveButton(R.string.settings) { _, _ ->
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", mActivity.packageName, null))
                        mActivity.startActivityForResult(intent, PermissionManager.PERMISSION_SETTINGS_REQUEST)

                    }
                    .setNegativeButton(android.R.string.cancel, null)
                    .setCancelable(false)
                    .show()
        else if (mOnPermissionResultListener != null)
            mOnPermissionResultListener!!.onPermissionBlocked(mPermissionsDenied)
        else
            mOnPermissionsBlockedFunction?.invoke()

        mPermissionBlocked = true
    }

    /**
     *  This method will be called internally when permissions are granted.
     */
    private fun onPermissionGranted() {

        if (enableLogs)
            Log.d(TAG, "onPermissionGranted")

        if (mOnPermissionResultListener == null && mOnPermissionsGrantedFunction == null)
            Log.e(TAG, mActivity.getString(R.string.onPermissionGranted_not_provided))
        else if (mOnPermissionResultListener != null)
            mOnPermissionResultListener!!.onPermissionGranted()
        else
            mOnPermissionsGrantedFunction?.invoke()

        mPermissionBlocked = false
    }

    companion object {
        const val PERMISSION_SETTINGS_REQUEST = 20
        const val PERMISSION_REQUEST = 10
    }
}