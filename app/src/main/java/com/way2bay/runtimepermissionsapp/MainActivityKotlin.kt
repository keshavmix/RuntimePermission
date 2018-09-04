package com.way2bay.runtimepermissionsapp

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import com.way2bay.runtimepermissions.PermissionManager

class MainActivityKotlin : AppCompatActivity() {

    lateinit var mPermissionManager: PermissionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //initialize permission manager with required permissions.
        mPermissionManager = PermissionManager(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))

        //add required permissions.
        mPermissionManager.addPermission(Manifest.permission.CAMERA)

        //enabled logging.
        mPermissionManager.enableLogs(true)

        //set method to be executed when permission granted by user.
        mPermissionManager.executeOnPermissionGranted { run { permissionsGranted() } }
        //set method to be executed when permission denied by user.
        mPermissionManager.executeOnPermissionDenied { run { permissionsDenied() } }
        //set method to be executed when permission blocked by user.
        mPermissionManager.executeOnPermissionBlocked { run { permissionsBlocked() } }

        //set PermissionListener for callbacks.
//        mPermissionManager.setPermissionListener(object : OnPermissionResultListener {
//            override fun onPermissionGranted() {
//                //code to execute
//                permissionsGranted()
//            }
//
//            override fun onPermissionDenied(permissions: ArrayList<String>) {
//                //show alert to ask permission again.
//                permissionsDenied()
//            }
//
//            override fun onPermissionBlocked(permissions: ArrayList<String>) {
//                /*Permission was denied and user checked Do not ask again.
//                You should ask and navigate user to Settings screen to enable permission.*/
//                permissionsBlocked()
//            }
//        })

        //check and request required permissions.
        mPermissionManager.checkAndRequestPermissions()

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        //set requestPermissionsResult params to check permissionManager.
        mPermissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //call OnActivityResult on permission manager to recheck when user returned from Settings screen.
        mPermissionManager.onActivityResult(requestCode)
    }

    fun permissionsGranted() {
        println("codeToExecute 1")
        println("codeToExecute 2")
        println("codeToExecute 3")
    }

    fun permissionsBlocked() {
        AlertDialog.Builder(this)
                .setMessage(com.way2bay.runtimepermissions.R.string.permission_blocked)
                .setPositiveButton(com.way2bay.runtimepermissions.R.string.settings, DialogInterface.OnClickListener { dialogInterface, i ->
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts("package", packageName, null))
                    startActivityForResult(intent, PermissionManager.PERMISSION_SETTINGS_REQUEST)

                })
                .setNegativeButton(android.R.string.cancel, null)
                .setCancelable(false)
                .show()
    }

    fun permissionsDenied() {
        AlertDialog.Builder(this)
                .setMessage(com.way2bay.runtimepermissions.R.string.permission_required)
                .setPositiveButton(com.way2bay.runtimepermissions.R.string.grant, DialogInterface.OnClickListener { dialogInterface, i -> mPermissionManager.checkAndRequestPermissions() })
                .setNegativeButton(android.R.string.cancel, null)
                .setCancelable(false)
                .show()
    }
}
