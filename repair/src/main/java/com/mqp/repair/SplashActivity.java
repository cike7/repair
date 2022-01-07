package com.mqp.repair;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

/**
 * 检查最新的资源包
 *
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public void onResume() {
        super.onResume();
        PermissionUtils.requestPermission(this, PermissionUtils.CODE_WRITE_EXTERNAL_STORAGE, permissionGrant);
    }


    private PermissionUtils.PermissionGrant permissionGrant = new PermissionUtils.PermissionGrant() {
        @Override
        public void onPermissionGranted(int requestCode) {
            switch (requestCode) {
                case PermissionUtils.CODE_WRITE_EXTERNAL_STORAGE:
                    //获取权限
                    init();
                    break;
                default:
            }
        }
    };

    private void init() {

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, TestActivity.class));
                finish();
            }
        }, 200);

    }

    /**
     * 请求权限回调方法
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        PermissionUtils.requestPermissionsResult(this, requestCode, permissions, grantResults, permissionGrant);
    }
}
