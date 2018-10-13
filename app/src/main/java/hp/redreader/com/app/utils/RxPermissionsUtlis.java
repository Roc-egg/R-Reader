package hp.redreader.com.app.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;

import com.tbruyelle.rxpermissions2.RxPermissions;

import timber.log.Timber;

/**
 * 类名：    RxPermissionsUtlis.java
 *
 * 类描述：  RxPermissions权限工具
 * 创建人：  hp
 * 创建时间：2018/10/13/013 19:16
 * 修改人：  hp
 * 修改时间：2018/10/13/013 19:16
 * 修改备注：
 */

public class RxPermissionsUtlis {
    private static final String TAG = "RxPermissionsUtlis";
    private final String message;
    private final PermissionCallbacks callbacks;
    private final String[] permissions;
    private Activity context;
    private RxPermissions rxPermission;
    private AlertDialog dialog;
    private AlertDialog.Builder builder;
    private int cont;

    /**
     * RxPermissions权限工具
     *
     * @param context     activity
     * @param message     权限文字
     * @param callbacks   回调
     * @param permissions 权限组
     */

    public RxPermissionsUtlis(Activity context, String message, final PermissionCallbacks callbacks, String... permissions) {
        this.context = context;
        this.message = message;
        this.callbacks = callbacks;
        this.permissions = permissions;
        if (rxPermission == null) {
            rxPermission = new RxPermissions(context);
        }
        if (builder == null) {
            builder = new AlertDialog.Builder(context);
        }
        gotoRequest();
    }

    @SuppressLint("CheckResult")
    private void gotoRequest() {
        cont = 0;
        rxPermission.requestEach(permissions)
                .subscribe(permission -> {
                    if (permission.granted) {
                        cont++;
                        if (cont == permissions.length) {
                            // 用户已经同意该权限
                            callbacks.onPermissionsGranted();
                        }
                        Timber.tag(TAG).e("%s is granted.", permission.name);
                    } else if (permission.shouldShowRequestPermissionRationale) {
                        // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框
                        Timber.tag(TAG).e("%s is denied. More info should be provided.", permission.name);
                        showDialog();
                    } else {
                        // 用户拒绝了该权限，并且选中『不再询问』
                        Timber.tag(TAG).e("%s is denied.", permission.name);
                        showSettingDialog();
                    }
                });
    }


    public interface PermissionCallbacks {

        void onPermissionsGranted();

//        void onPermissionsDenied(int requestCode, List<String> perms);
//
//        void onPermissionsAllGranted();

    }

    /**
     * 启动设置界面的dialog
     */
    private void showSettingDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
        dialog = builder.setMessage("使用此功能需要" + message + "权限，请去设置")
                .setPositiveButton("去设置", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                    intent.setData(uri);
                    context.startActivity(intent);
                })
                .setNegativeButton("取消", (dialog, which) -> dialog.dismiss())
                .create();
        dialog.show();
    }

    /**
     * 拒绝权限的提示dialog
     */
    private void showDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
        dialog = builder.setMessage("使用此功能需要开启" + message + "权限")
                .setPositiveButton("确定", (dialog, which) -> {
                    dialog.dismiss();
                    gotoRequest();
                })
                .setNegativeButton("取消", (dialog, which) -> dialog.dismiss())
                .create();
        dialog.show();
    }
}
