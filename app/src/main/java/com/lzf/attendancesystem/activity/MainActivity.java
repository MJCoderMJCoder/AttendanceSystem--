package com.lzf.attendancesystem.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.arcsoft.face.ActiveFileInfo;
import com.arcsoft.face.ErrorInfo;
import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.enums.RuntimeABI;
import com.lzf.attendancesystem.R;
import com.lzf.attendancesystem.ZffApplication;
import com.lzf.attendancesystem.util.ConfigUtil;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.List;

import static com.arcsoft.face.enums.DetectFaceOrientPriority.ASF_OP_ALL_OUT;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.
    //    static {
    //        System.loadLibrary("native-lib");
    //    }

    /**
     * 所请求的一系列权限
     */
    private final String[] PERMISSIONS = new String[]{
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,
            Manifest.permission.MOUNT_FORMAT_FILESYSTEMS,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.MEDIA_CONTENT_CONTROL,
            Manifest.permission.MANAGE_DOCUMENTS,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.INTERNET};

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
    }

    public void onClick(final View view) {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) {
                RuntimeABI runtimeABI = FaceEngine.getRuntimeABI();
                Log.i("MJCoder", "subscribe: getRuntimeABI() " + runtimeABI);
                int activeCode = FaceEngine.activeOnline(MainActivity.this, "GMZPhEArrLoVVb8gtJ1KydUPRdgK4JkZVXh77WKvGFQD", "2EUD77P6jAr2TpAU372yd26ASB18pEZbeRFnPCsPFZTN");
                emitter.onNext(activeCode);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(Integer activeCode) {
                if (activeCode == ErrorInfo.MOK) {
                    //Toast.makeText(MainActivity.this, "激活引擎成功", Toast.LENGTH_SHORT).show();
                    ConfigUtil.setFtOrient(MainActivity.this, ASF_OP_ALL_OUT);
                    switch (view.getId()) {
                        case R.id.attendance:
                            startActivity(new Intent(MainActivity.this, AttendanceActivity.class));
                            break;
                        case R.id.adminLogin:
                            startActivity(new Intent(MainActivity.this, AdminLoginActivity.class));
                            break;
                        default:
                            break;
                    }
                } else if (activeCode == ErrorInfo.MERR_ASF_ALREADY_ACTIVATED) {
                    //Toast.makeText(MainActivity.this, "引擎已激活，无需再次激活", Toast.LENGTH_SHORT).show();
                    ConfigUtil.setFtOrient(MainActivity.this, ASF_OP_ALL_OUT);
                    switch (view.getId()) {
                        case R.id.attendance:
                            startActivity(new Intent(MainActivity.this, AttendanceActivity.class));
                            break;
                        case R.id.adminLogin:
                            startActivity(new Intent(MainActivity.this, AdminLoginActivity.class));
                            break;
                        default:
                            break;
                    }
                } else {
                    Toast.makeText(MainActivity.this, "引擎激活失败，错误码为：" + activeCode, Toast.LENGTH_LONG).show();
                }
                ActiveFileInfo activeFileInfo = new ActiveFileInfo();
                int res = FaceEngine.getActiveFileInfo(MainActivity.this, activeFileInfo);
                if (res == ErrorInfo.MOK) {
                    Log.i("MJCoder", activeFileInfo.toString());
                }
            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onComplete() {
            }
        });
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
