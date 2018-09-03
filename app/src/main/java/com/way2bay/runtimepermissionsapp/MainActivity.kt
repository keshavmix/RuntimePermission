package com.way2bay.runtimepermissionsapp

import android.Manifest
import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import com.way2bay.runtimepermissions.OnPermissionResultListener
import com.way2bay.runtimepermissions.PermissionManager
import java.util.*

class MainActivity : AppCompatActivity() {

    var testString: String? = null

    lateinit var lateTestString: String

    val lazyString: String by lazy { "Lazy test" }

    lateinit var mPermissionManager: PermissionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        println(testString?.length)

        lateTestString = "test"
        println(lateTestString.length)

        println(lazyString)

        mPermissionManager = PermissionManager(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE))

        mPermissionManager.setPermissionListener(object : OnPermissionResultListener {
            override fun onPermissionGranted() {

                //code to execute
                println("onPermissionGranted")
            }

            override fun onPermissionDenied(permissions: ArrayList<String>) {
                //show mSnackbar to ask permission again.
                println("onPermissionDenied")
                AlertDialog.Builder(this@MainActivity)
                        .setMessage(R.string.permission_required)
                        .setPositiveButton(R.string.grant, DialogInterface.OnClickListener { dialogInterface, i -> mPermissionManager.checkAndRequestPermissions() })
                        .setNegativeButton(android.R.string.cancel, null)
                        .setCancelable(false)
                        .show()

            }

            override fun onPermissionBlocked(permissions: ArrayList<String>) {
                //Permission was denied and user checked Do not ask again.
                println("onPermissionBlocked")

            }
        })

        mPermissionManager.checkAndRequestPermissions()

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        mPermissionManager.checkPermissionResult(requestCode, permissions, grantResults)
    }

}
