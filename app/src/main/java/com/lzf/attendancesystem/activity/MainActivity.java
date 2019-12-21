package com.lzf.attendancesystem.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.arcsoft.face.ErrorInfo;
import com.lzf.attendancesystem.R;
import com.lzf.attendancesystem.ZffApplication;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.
    //    static {
    //        System.loadLibrary("native-lib");
    //    }

    /**
     * 所请求的一系列权限
     */
    private final String[] PERMISSIONS = new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS, Manifest.permission.MOUNT_FORMAT_FILESYSTEMS, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.MEDIA_CONTENT_CONTROL, Manifest.permission.MANAGE_DOCUMENTS, Manifest.permission.READ_PHONE_STATE, Manifest.permission.INTERNET};

    /**
     * 声明一个集合，在后面的代码中用来存储用户拒绝授权的一系列权限
     */
    private List<String> permissionList = new ArrayList<String>();

    /**
     * 请求权限的编号代码
     */
    public final int REQUEST_PERMISSION = 6003;
    //    public static final int FLAG_HOMEKEY_DISPATCHED = 0x80000000; //需要自己定义标志

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //        Log.v(stringFromJNI(), stringFromJNI());
        permissionIsGranted();

        new Thread() {
            @Override
            public void run() {
                super.run();
                int temp = ZffApplication.getFaceEngine().active(MainActivity.this, "GMZPhEArrLoVVb8gtJ1KydUPRdgK4JkZVXh77WKvGFQD", "2EUD77P6jAr2TpAU372yd26ASB18pEZbeRFnPCsPFZTN");
                //激活设备，一个设备安装后仅需激活一次，卸载重新安装后需要重新激活。
                while (ErrorInfo.MOK != temp) {
                    temp = ZffApplication.getFaceEngine().active(MainActivity.this, "GMZPhEArrLoVVb8gtJ1KydUPRdgK4JkZVXh77WKvGFQD", "2EUD77P6jAr2TpAU372yd26ASB18pEZbeRFnPCsPFZTN");
                }
            }
        }.start();
    }

    public void onClick(View view) {
        //激活设备，一个设备安装后仅需激活一次，卸载重新安装后需要重新激活。
        if (ErrorInfo.MOK != ZffApplication.getFaceEngine().active(this, "GMZPhEArrLoVVb8gtJ1KydUPRdgK4JkZVXh77WKvGFQD", "2EUD77P6jAr2TpAU372yd26ASB18pEZbeRFnPCsPFZTN")) {
            ZffApplication.getFaceEngine().active(this, "GMZPhEArrLoVVb8gtJ1KydUPRdgK4JkZVXh77WKvGFQD", "2EUD77P6jAr2TpAU372yd26ASB18pEZbeRFnPCsPFZTN");
        }
        switch (view.getId()) {
            case R.id.attendance:
                startActivity(new Intent(this, AttendanceActivity.class));
                break;
            case R.id.adminLogin:
                startActivity(new Intent(this, AdminLoginActivity.class));
                break;
            default:
                break;
        }
    }

    /**
     * 判断哪些权限未授予以便在必要的时候重新申请
     * 判断存储未授予权限的集合permissionList是否为空：未授予的权限为空，表示都授予了
     */
    private void permissionIsGranted() {
        permissionList.clear();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String permission : PERMISSIONS) {
                if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) { //该权限已经授予
                    //判断是否需要 向用户解释，为什么要申请该权限
                    ActivityCompat.shouldShowRequestPermissionRationale(this, permission);
                    permissionList.add(permission);
                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            permission)) {
                        Toast.makeText(this, "获取权限失败，请在“设置”-“应用权限”-打开所需权限", Toast.LENGTH_LONG).show();
                    }
                }
            }
            if (!permissionList.isEmpty()) {
                String[] permissions = new String[permissionList.size()];
                //请求权限
                ActivityCompat.requestPermissions(this, permissionList.toArray(permissions), REQUEST_PERMISSION);
            }
        }
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    //    public native String stringFromJNI();
}
