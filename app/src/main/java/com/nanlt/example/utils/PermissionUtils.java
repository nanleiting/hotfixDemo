package com.nanlt.example.utils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PermissionUtils {
    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;
    final private int REQUEST_PERMISSION_SETTING = 125;//跳转到设置
    private final Map<String, String> map;
    private List<String> permissionsList;//存储 还没有授权的 权限
    private List<String> nameList;//存储 还没有授权的 权限 的名字
    private Activity activity;

    //权限库
    private String getPermissionName(String key) {
        return map.get(key);
    }

    public PermissionUtils(Activity activity) {
        permissionsList = new ArrayList<String>();
        nameList = new ArrayList<String>();
        this.activity = activity;
        map = new HashMap<>();
        initData();
    }

    private void initData() {
        map.put(Manifest.permission.CAMERA, "相机");
        map.put(Manifest.permission.RECORD_AUDIO, "麦克风");
        map.put(Manifest.permission.READ_PHONE_STATE, "电话");
        map.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, "读写");
        map.put(Manifest.permission.ACCESS_FINE_LOCATION, "GPS");
        map.put(Manifest.permission.READ_CONTACTS, "读取");
        map.put(Manifest.permission.WRITE_CONTACTS, "写入");
    }

    /**
     * 添加权限
     *
     * @param permission
     * @return
     */
    public PermissionUtils addPermissionsList(String permission) {
        if (TextUtils.isEmpty(permission)) {
            return this;
        }
        if (checkPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            nameList.add(getPermissionName(permission));
        }
        return this;
    }

    /**
     * 开始申请权限
     */
    public void startRequestPermissions() {
        if (permissionsList != null && permissionsList.size() > 0 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
        }
    }

    /**
     * 检查权限
     *
     * @param permission
     * @return
     */
    private int checkPermission(String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return activity.checkSelfPermission(permission);
        }
        return 0;
    }

    /**
     * 处理权限申请结果
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    public void onPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS) {
            return;
        }

        if (grantResults.length > 0) {
//            permissionsList = new ArrayList<>();//保存 没有 授权的 权限
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    permissionsList.remove(permissions[i]);
                }
            }

            if (permissionsList.isEmpty()) {
                //已经全部授权
                permissionAllGranted();
            } else {
                //勾选了对话框中”Don’t ask again”的选项, 返回false
                for (String deniedPermission : permissionsList) {
                    boolean flag = true;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        flag = activity.shouldShowRequestPermissionRationale(deniedPermission);
                    }
                    if (!flag) {
                        //拒绝授权
                        permissionShouldShowRationale(permissionsList);
                        return;
                    }
                }
                //拒绝授权
                permissionHasDenied(permissionsList);

            }
        }

    }

    /**
     * 处理 进入 被拒绝后 进入设置界面 的回调 结果
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode != REQUEST_PERMISSION_SETTING) {
            return;
        }
        if (getNotBeenGranted(permissionsList) != null && getNotBeenGranted(permissionsList).size() > 0) {
//            Toast.makeText(activity, "还有权限被拒绝", Toast.LENGTH_SHORT)
//                    .show();
            permissionShouldShowRationale(getNotBeenGranted(permissionsList));
        }

    }

    /**
     * 检查 列表中 是否还有 未授权 的权限 并 清理已 授权 的权限
     *
     * @return
     */
    private List<String> getNotBeenGranted(List<String> list) {
        Iterator<String> iterator = list.iterator();
        while (iterator.hasNext()) {
            String value = iterator.next();
            if (checkPermission(value) == PackageManager.PERMISSION_GRANTED) {
                iterator.remove();
            }
        }
        return list;
    }

    /**
     * 获取 未授权 的权限的名字
     *
     * @return
     */
    private List<String> getNotBeenGrantedNames(List<String> list) {
        List<String> names = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            names.add(getPermissionName(list.get(i)));
        }
        return names;
    }


/////////////////////处理授权结果///////////////////////////

    /**
     * 有权限被拒绝
     *
     * @param deniedPermissionList 被拒绝的权限
     */

    private void permissionHasDenied(List<String> deniedPermissionList) {
        if (deniedPermissionList != null && deniedPermissionList.size() > 0 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.requestPermissions(deniedPermissionList.toArray(new String[deniedPermissionList.size()]),
                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
        }
    }

    /**
     * 权限被拒绝并且勾选了不在询问
     *
     * @param deniedPermissionList 勾选了不在询问的权限
     */
    private void permissionShouldShowRationale(List<String> deniedPermissionList) {
        List<String> nameList = getNotBeenGrantedNames(deniedPermissionList);
        if (nameList != null && nameList.size() > 0) {
            String message = "请前往“设置界面”授权以下权限：\n" + nameList.get(0);
            for (int i = 1; i < nameList.size(); i++) {
                message = message + "\n " + nameList.get(i);
            }
            new AlertDialog.Builder(activity)
                    .setMessage(message)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //跳转到设置页面 去处理
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                            intent.setData(uri);
                            activity.startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                        }
                    })
                    .setNegativeButton("取消", null)
                    .create()
                    .show();

        }
    }

    /**
     * 已经全部授权
     */
    private void permissionAllGranted() {
        Toast.makeText(activity, "已经全部授权", Toast.LENGTH_SHORT)
                .show();
    }

}
