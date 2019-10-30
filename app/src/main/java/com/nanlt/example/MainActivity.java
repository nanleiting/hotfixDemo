package com.nanlt.example;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.nanlt.example.utils.PermissionUtils;


public class MainActivity extends BaseActivity {
   private PermissionUtils permissionUtils;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //运行时权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permissionUtils = new PermissionUtils(this);
            permissionUtils
                    .addPermissionsList(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .startRequestPermissions();

        }
    }

    public void jump(View view) {
        startActivity(new Intent(this, SecondActivity.class));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionUtils.onPermissionsResult(requestCode, permissions, grantResults);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        permissionUtils.onActivityResult(requestCode, resultCode, data);
    }
}
