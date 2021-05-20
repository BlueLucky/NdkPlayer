package com.qizhidao.vendor.ndkplayer;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;


import java.util.ArrayList;
import java.util.concurrent.TimeUnit;




/**
 * @author tao.peng
 * @date 19.3.27
 * <p>
 * UtilPermission 直接使用的是原生方法处理，添加的代码比较多，适合在意启动速度的地方
 * <p>
 * UtilRxPermission 使用添加Fragmnet来实现权限动态申请，使用比较方便，会影响界面添加速度
 */

public final class UtilPermission {

    public static final int REQUEST_PERMISSIONS = 100;
    public static final int REQUEST_PERMISSIONS_CONTRACT = 101;

    /**
     * homeActivity 中的权限管理
     */
    public static final int REQUEST_PERMISSIONS_HOME = 102;
    /**
     * splash 中的权限管理
     */
    public static final int REQUEST_PERMISSIONS_SPLASH = 103;

    private UtilPermission() {
    }

    /**
     * 手机是否开启位置服务，如果没有开启那么所有app将不能使用定位功能
     */
    public static boolean isLocServiceEnable(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        return gps || network;
    }

    /**
     * 首页只申请
     */
    public static void requestHomePermission(Activity activity, PermissionRequestListener listener) {
        requestPermission(activity, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
                Manifest.permission.ACCESS_FINE_LOCATION
        }, REQUEST_PERMISSIONS_HOME, listener);
    }
    /**
     * 被拒绝后，只申请 外部存储的权限了
     * @param activity
     * @param listener
     */
    public static void requestHomeExternalPermission(Activity activity, PermissionRequestListener listener){
        requestPermission(activity, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        }, REQUEST_PERMISSIONS_HOME, listener);
    }
    public static boolean isLocationPermission(String permission) {
        return Manifest.permission.ACCESS_COARSE_LOCATION.equals(permission)
                || Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS.equals(permission)
                || Manifest.permission.ACCESS_FINE_LOCATION.equals(permission);
    }

    public static boolean isFileExternalPermission(String permission) {
        return Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(permission)
                || Manifest.permission.READ_EXTERNAL_STORAGE.equals(permission);
    }


    /**
     * 检查首页必要权限是否受理了
     *
     * @param activity
     * @return
     */
    public static boolean isHomeNecessityPermissionGrant(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    /**
     * 获取闪屏界面权限，强制升级的时候，需要去获取
     *
     * @param activity
     * @param listener
     */
    @Deprecated
    public static void requestSplashPermission(Activity activity, PermissionRequestListener listener) {
        requestPermission(activity, new String[]{
                /* Manifest.permission.READ_PHONE_STATE,*/
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS, listener);
    }

    /**
     * 闪屏页面不需要相关的读取权限了
     *
     * @param fragment
     * @param listener
     */
    @Deprecated
    public static void requestSplashPermission(Fragment fragment, PermissionRequestListener listener) {
        requestPermission(fragment, new String[]{
                /* Manifest.permission.READ_PHONE_STATE,*/
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS_SPLASH, listener);
    }


    /**
     * 请求定位权限
     */
    public static void requestLocationPermission(Activity activity, PermissionRequestListener listener) {
        requestPermission(activity, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
                Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSIONS, listener);
    }


    public static void requestPermission(Activity activity, String permissionStr[], int requestCode, PermissionRequestListener listener) {
        //      这里申请权限，如果通过，直接执行下一步处理
        if (requestPermission(activity, requestCode, permissionStr)) {
            if (listener != null)
                listener.finallyRequestedPermission(requestCode);
        }
    }


    /**
     * 通过Fragment请求的  requestCode 必须要到 Fragment 中处理才是一致的，与Activity中的requestCode是不一致的
     *
     * @param fragment
     * @param permissionStr
     * @param requestCode
     * @param listener
     */
    public static void requestPermission(Fragment fragment, String permissionStr[], int requestCode, PermissionRequestListener listener) {
        //      这里申请权限，如果通过，直接执行下一步处理
        if (requestPermission(fragment, requestCode, permissionStr)) {
            if (listener != null) {
                listener.finallyRequestedPermission(requestCode);
            }
        }
    }

    /**
     * 权限请求
     * 支持 Activity  和 Fragment 处理
     */
    private static boolean requestPermission(Object activity, int askPermissionCode, String[] permissionStr) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            final ArrayList<String> permissionsList = new ArrayList<>();
            for (String permission : permissionStr) {
                if (activity instanceof Activity) {
                    if (((Activity) activity).checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                        permissionsList.add(permission);
                    }
                } else if (activity instanceof Fragment) {
                    if (((Fragment) activity).getContext().checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                        permissionsList.add(permission);
                    }
                }
            }
            if (permissionsList.size() == 0) {
                return true;
            } else {
                if (activity instanceof Activity) {
                    ((Activity) activity).requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                            askPermissionCode);
                } else if (activity instanceof Fragment) {
                    ((Fragment) activity).requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                            askPermissionCode);
                }
                return false;
            }
        }
        return true;
    }

    /**
     * 新增
     *
     * @param activity
     * @param requestCode
     * @param permissions
     * @param grantResults
     * @param listener
     */
    public static void onRequestPermissionsResult(Object activity, int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults, PermissionRequestListener listener) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && grantResults.length > 0) {
            int size = grantResults.length;
            for (int i = 0; i < size; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    if (activity instanceof Activity) {
                        Activity target = (Activity) activity;
                        if (!target.shouldShowRequestPermissionRationale(permissions[i])) {
                            //                      用户点击了不在提醒按钮，并拒绝后，会调用到这里，这时候弹出提示框，引导用户去系统设置页去开启权限
                            deniedPermissionNoSystemDialog(target, requestCode, permissions[i], listener);
                        } else {
                            if (listener != null)
                                listener.finallyDeniedPermission(requestCode, permissions[i]);
                        }
                    } else if (activity instanceof Fragment) {
                        Fragment target = (Fragment) activity;
                        if (!target.shouldShowRequestPermissionRationale(permissions[i])) {
                            //                      用户点击了不在提醒按钮，并拒绝后，会调用到这里，这时候弹出提示框，引导用户去系统设置页去开启权限
                            deniedPermissionNoSystemDialog(target.requireActivity(), requestCode, permissions[i], listener);
                        } else {
                            if (listener != null)
                                listener.finallyDeniedPermission(requestCode, permissions[i]);
                        }
                    }
                    return;
                }
            }
            if (listener != null) {
                listener.finallyRequestedPermission(requestCode);
            }
        } else {
            //grantResults.length =  0  授权过程被打断，暂时不处理这里面的业务逻辑
            if (listener != null) {
                listener.finallyRequestedPermission(requestCode);
            }
        }
    }

    public static void onRequestPermissionsResult(Activity activity, int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults, PermissionRequestListener listener) {

        // 判断是否是内部fragment申请的权限
        int index = (requestCode >> 16) & 0xffff;
        // 如果不是内部申请的权限，只是activity申请的，就走下面的方法
        if (index != 0) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && grantResults.length > 0) {
            int size = grantResults.length;
            for (int i = 0; i < size; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    if (!activity.shouldShowRequestPermissionRationale(permissions[i])) {
                        boolean isIntercept = false;
                        if (listener != null) {
                            isIntercept = listener.forEverDeniedPermissionPermission(requestCode, permissions[i]);
                        }
                        if (!isIntercept) {
                            // 用户点击了不在提醒按钮，并拒绝后，会调用到这里，这时候弹出提示框，引导用户去系统设置页去开启权限
                            deniedPermissionNoSystemDialog(activity, requestCode, permissions[i], listener);
                        }
                    } else {
                        if (listener != null)
                            listener.finallyDeniedPermission(requestCode, permissions[i]);
                    }

                    return;
                }
            }
            //          如果所有的权限都容许后，后续逻辑处理
            if (listener != null)
                listener.finallyRequestedPermission(requestCode);
        } else {
            //              如果容许后，后续逻辑处理
            if (listener != null)
                listener.finallyRequestedPermission(requestCode);
        }
    }


    /*
     * 权限被拒绝后的处理,并是不在提示时
     * 这里再次请求ActivityCompat.requestPermissions(DynamicPermissionsActivity.this, new String[]{permission}, REQUEST_EXTERNAL_STORAGE);后
     * 不会弹出系统的权限选择框，这里要跳到该应用的权限设置页处理
     */
    private static void deniedPermissionNoSystemDialog(Activity activity, int requestCode, String permission, PermissionRequestListener listener) {
        //        String tip = UtilRxPermission.getNoPermissionTip(activity, permission);
        //        CommonTipTwoRightBtnDialog dialog = new CommonTipTwoRightBtnDialog(activity, "权限申请", tip, "取消", null, "开启", null, (isEnsure) -> {
        //            if (isEnsure) {
        //                startPermissionManager(activity, false);
        //            } else {
        //                if (listener != null) {
        //                    listener.finallyDeniedPermission(requestCode, permission);
        //                }
        //            }
        //        });
        //        dialog.setCancelable(false);
        //        dialog.setCanceledOnTouchOutside(false);
        //        dialog.setAutoDismiss(true);
        //        dialog.show();
    }

    /**
     * 跳转到应用权限申请管理界面
     *
     * @param activity
     * @param killApp  是否需要退出app
     */
    public static void startPermissionManager(Activity activity, boolean killApp) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + activity.getPackageName())); // 根据包名打开对应的设置界面
        activity.startActivity(intent);
        //        if (killApp) {
        //            ToastUtils.showToast(activity, activity.getString(R.string.need_permission));
        //            //                    activity.finish();
        //            //                  延迟1秒关闭掉进程，直接使用finish()时，比较短的时间再次点击进入HomeActivity页面时，会触发onNewIntent
        //            Observable.timer(1000, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(aLong -> {
        //                ActivityManagerUtil.finishActivity();
        //            }, ex -> {
        //            });
        //        }
    }


    public interface PermissionRequestListener {


        /**
         * 表示已获取到指定的权限
         *
         * @param requestCode 请求code
         */
        void finallyRequestedPermission(int requestCode);


        /**
         * 最终没有获取该权限调用
         *
         * @param requestCode 多个请求时区分
         * @param permission  拒绝的权限
         */
        void finallyDeniedPermission(int requestCode, String permission);

        /**
         * 永久被拒绝后的回调信息
         * 目前只在首页中处理回调
         *
         * @return true  劫持默认弹框处理
         */
        boolean forEverDeniedPermissionPermission(int requestCode, String permission);


    }


}
