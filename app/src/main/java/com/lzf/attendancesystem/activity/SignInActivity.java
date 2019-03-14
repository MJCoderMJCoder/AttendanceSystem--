package com.lzf.attendancesystem.activity;

import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import com.lzf.attendancesystem.R;

public class SignInActivity extends AppCompatActivity {
    private Camera camera; //照相机（硬件）对象
    private SurfaceView surfaceView; //预览视图

    private SurfaceHolder.Callback surfaceHolderCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) { //开始预览
            try {
                if (camera == null) {
                    Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
                    for (int cameraIndex = 0; cameraIndex < Camera.getNumberOfCameras(); cameraIndex++) {
                        Camera.getCameraInfo(cameraIndex, cameraInfo);
                        /*if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                            camera = Camera.open(cameraIndex); //前置摄像头
                            camera.setDisplayOrientation(270);   //让相机旋转270度
                            break;
                        } else*/
                        if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                            camera = Camera.open(cameraIndex); //前置摄像头
                            camera.setDisplayOrientation(90);   //让相机旋转90度
                            break;
                        }
                    }
                }
                camera.setPreviewDisplay(surfaceView.getHolder());
                camera.startPreview();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            if (camera != null) {
                camera.stopPreview();
                camera.release();
                camera = null;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        if (checkCamera()) {
            surfaceView = findViewById(R.id.surfaceView);
            surfaceView.getHolder().addCallback(surfaceHolderCallback);
        } else {
            Toast.makeText(this, "抱歉，摄像头不可用", Toast.LENGTH_SHORT).show();
            finish();
        }
    }


    /**
     * 摄像头是否存在
     *
     * @return true：摄像头存在
     */
    private boolean checkCamera() {
        return this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }
}
