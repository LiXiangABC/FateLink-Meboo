package com.crush.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.crush.dialog.RequestPermissionDialog;
import com.custom.base.util.ToastUtil;
import com.crush.R;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import java.util.List;

import io.rong.imkit.activity.Activities;
import io.rong.imkit.utils.RongUtils;


/**
 * @author
 * @date 2018/12/5
 */
public class PermissionUtils {

    /**
     * 判断是否缺少权限
     */
    public static boolean lacksPermission(String permission) {
        FragmentActivity activity = Activities.Companion.get().getTop();
        if (RongUtils.isDestroy(activity)){
            return true;
        }
        return ActivityCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_DENIED;
    }

    @SuppressLint("WrongConstant")
    public static void requestPermission(Activity context, int type, Action<List<String>> granted,
                                         String... permissions) {
        AndPermission.with(context)
                .runtime()
                .permission(permissions)
                .onGranted(granted)
                .onDenied(denied -> {
                    if (AndPermission.hasAlwaysDeniedPermission(context, denied)) {
                        if (!CollectionUtils.isEmpty(denied)) {
                            new RequestPermissionDialog(context,type).showPopupWindow();
                        }
                    }
                }).start();
    }

    public static void goSetting(Context context) {
        AndPermission.with(context).runtime().setting().start(1099);
    }

    public static void requestPermission(Context context, Action<List<String>> granted,
                                         Action<List<String>> denied, String... permissions) {
        AndPermission.with(context)
                .runtime()
                .permission(permissions)
                .onGranted(granted)
                .onDenied(denied)
                .start();
    }

    /**
     * 获取应用必须的权限：读写权限、手机信息权限
     */
    public static void requestMustPermission(Context context, Action<List<String>> granted,
                                             Action<List<String>> denied) {
//        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.P){
//            PermissionUtils.requestPermission(context, data -> {
//                PermissionUtils.requestPermission(context, granted, denied, Permission.READ_PHONE_STATE);
//            }, denied, Permission.Group.STORAGE);
//        } else {
            PermissionUtils.requestPermission(context, granted, denied, Permission.Group.STORAGE);
//        }
    }

    public static boolean hasMustPermission(Context context) {
        return AndPermission.hasPermissions(context, Permission.Group.STORAGE)
                && AndPermission.hasPermissions(context, Permission.READ_PHONE_STATE);
    }
}
