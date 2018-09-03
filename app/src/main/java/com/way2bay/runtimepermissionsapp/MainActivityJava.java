package com.way2bay.runtimepermissionsapp;

import android.os.Bundle;
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
        mPermissionManager = new PermissionManager(this);

        //enabled logging
        mPermissionManager.enableLogs(true);

        //set method to be executed when permission granted by user
        mPermissionManager.executeOnPermissionGranted(new Function0<Unit>() {
            @Override
            public Unit invoke() {

                return null;
            }
        });

        //set method to be executed when permission blocked by user
        mPermissionManager.executeOnPermissionBlocked(new Function0<Unit>() {
            @Override
            public Unit invoke() {
                return null;
            }
        });

        //set method to be executed when permission denied by user
        mPermissionManager.executeOnPermissionDenied(new Function0<Unit>() {
            @Override
            public Unit invoke() {
                return null;
            }
        });

    }
}
