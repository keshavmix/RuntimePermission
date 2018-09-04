package com.way2bay.runtimepermissionsapp;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.way2bay.runtimepermissions.PermissionManager;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;

public class MainActivityJava extends AppCompatActivity {


    private PermissionManager mPermissionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize permission manager with required permissions.
        mPermissionManager = new PermissionManager(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION});

        //add required permissions.
        mPermissionManager.addPermission(Manifest.permission.CAMERA);

        //enabled logging.
        mPermissionManager.enableLogs(true);

        //set method to be executed when permission granted by user.
        mPermissionManager.executeOnPermissionGranted(new Function0<Unit>() {
            @Override
            public Unit invoke() {
                permissionsGranted();
                return null;
            }
        });

        //set method to be executed when permission denied by user.
        mPermissionManager.executeOnPermissionDenied(new Function0<Unit>() {
            @Override
            public Unit invoke() {
                permissionsDenied();
                return null;
            }
        });

        //set method to be executed when permission blocked by user.
        mPermissionManager.executeOnPermissionBlocked(new Function0<Unit>() {
            @Override
            public Unit invoke() {
                permissionsBlocked();
                return null;
            }
        });

        //set PermissionListener for callbacks.
//        mPermissionManager.setPermissionListener(new OnPermissionResultListener() {
//
//            @Override
//            public void onPermissionGranted() {
//                //code to execute
//                permissionsGranted();
//            }
//
//            @Override
//            public void onPermissionDenied(@NotNull ArrayList<String> permissions) {
//                //show alert to ask permission again.
//                permissionsDenied();
//            }
//
//            @Override
//            public void onPermissionBlocked(@NotNull ArrayList<String> permissions) {
//                //Permission was denied and user checked Do not ask again.
//                permissionsBlocked();
//            }
//        });

        //check and request required permissions
        mPermissionManager.checkAndRequestPermissions();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //set requestPermissionsResult params to check permissionManager
        mPermissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //call OnActivityResult on permission manager to recheck when user returned from Settings screen.
        mPermissionManager.onActivityResult(requestCode);
    }

    private void permissionsGranted() {
        System.out.println("codeToExecute 1");
        System.out.println("codeToExecute 2");
        System.out.println("codeToExecute 3");
    }

    private void permissionsDenied() {
        new AlertDialog.Builder(this)
                .setMessage(com.way2bay.runtimepermissions.R.string.permission_required)
                .setPositiveButton(com.way2bay.runtimepermissions.R.string.settings, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mPermissionManager.checkAndRequestPermissions();
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .setCancelable(false)
                .show();
    }

    private void permissionsBlocked() {
        new AlertDialog.Builder(this)
                .setMessage(com.way2bay.runtimepermissions.R.string.permission_blocked)
                .setPositiveButton(com.way2bay.runtimepermissions.R.string.settings, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", getPackageName(), null));
                        startActivityForResult(intent, PermissionManager.PERMISSION_SETTINGS_REQUEST);
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .setCancelable(false)
                .show();
    }
}
