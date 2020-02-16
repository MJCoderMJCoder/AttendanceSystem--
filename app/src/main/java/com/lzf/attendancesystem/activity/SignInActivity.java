package com.lzf.attendancesystem.activity;

import android.content.pm.PackageManager;
import android.database.Cursor;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.arcsoft.face.AgeInfo;
import com.arcsoft.face.ErrorInfo;
import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.FaceFeature;
import com.arcsoft.face.FaceInfo;
import com.arcsoft.face.FaceSimilar;
import com.arcsoft.face.GenderInfo;
import com.arcsoft.face.LivenessInfo;
import com.arcsoft.face.enums.DetectFaceOrientPriority;
import com.arcsoft.face.enums.DetectMode;
import com.lzf.attendancesystem.R;
import com.lzf.attendancesystem.ZffApplication;
import com.lzf.attendancesystem.bean.Attendance;
import com.lzf.attendancesystem.bean.AttendanceDao;
import com.lzf.attendancesystem.bean.Staff;
import com.lzf.attendancesystem.bean.StaffDao;

import org.greenrobot.greendao.query.QueryBuilder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

public class SignInActivity extends AppCompatActivity {
    private int currentCameraID; //当前摄像头ID
    private int frontCameraId; //前置摄像头ID
    private int frontCameraDegrees; //前置摄像头要旋转的度数
    private int backCameraId; //后置摄像头ID
    private int backCameraDegrees; //后置摄像头要旋转的度数
    private Camera camera; //照相机（硬件）对象
    private MediaRecorder mediaRecorder; //录制视频
    private SurfaceView surfaceView; //预览视图
    private TextView age; //年龄显示
    private int ageValue = 0; //年龄检测结果
    private TextView gender; //性别显示
    private TextView liveness; //活体检测显示
    private String livenessInfo = ""; //活体检测结果
    private ImageView signInSuccess;
    private LinearLayout signInSuccessLinear;
    private TextView staffId;
    private TextView staffName;
    private TextView signInTime;
    private TextView signInSuccessT;
    private List<Staff> staffList = ZffApplication.getDaoSession(this).getStaffDao().queryBuilder().orderAsc(StaffDao.Properties.StaffId).list();

    private AttendanceDao attendanceDao = ZffApplication.getDaoSession(this).getAttendanceDao();
    private long today = System.currentTimeMillis() / (1000 * 3600 * 24) * (1000 * 3600 * 24) - TimeZone.getDefault().getRawOffset();//今天零点零分零秒的毫秒数
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
    private Camera.PreviewCallback previewCallback = new Camera.PreviewCallback() {
        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            /**
             * data - 输入的图像数据
             * width - 图像的宽度
             * height - 图像的高度
             * format - 图像的颜色空间格式，支持NV21(CP_PAF_NV21)、BGR24(CP_PAF_BGR24)
             * faceInfoList - 人脸列表，传入后赋值
             */
            List<FaceInfo> faceInfos = new ArrayList<FaceInfo>();
            Camera.Size previewSize = camera.getParameters().getPreviewSize();
            if (ZffApplication.getFaceEngine().detectFaces(data, previewSize.width, previewSize.height, FaceEngine.CP_PAF_NV21, faceInfos) == ErrorInfo.MOK && faceInfos.size() > 0) {
                for (FaceInfo faceInfo : faceInfos) {
                    FaceFeature currentFaceFeature = new FaceFeature();
                    if (ZffApplication.getFaceEngine().extractFaceFeature(data, previewSize.width, previewSize.height, FaceEngine.CP_PAF_NV21, faceInfo, currentFaceFeature) == ErrorInfo.MOK) {
                        FaceSimilar faceSimilar = new FaceSimilar();
                        for (Staff staff : staffList) {
                            FaceFeature compareFaceFeature = new FaceFeature();
                            compareFaceFeature.setFeatureData(staff.getStaffFaceOneFeatureData());
                            if (ZffApplication.getFaceEngine().compareFaceFeature(currentFaceFeature, compareFaceFeature, faceSimilar) == ErrorInfo.MOK) {
                                //                                Log.v("faceSimilar.getScore()", faceSimilar.getScore() + "");
                                if (faceSimilar.getScore() >= 0.65f) { //签到成功
                                    signInSuccess(staff);
                                }
                            }
                            compareFaceFeature.setFeatureData(staff.getStaffFaceTwoFeatureData());
                            if (ZffApplication.getFaceEngine().compareFaceFeature(currentFaceFeature, compareFaceFeature, faceSimilar) == ErrorInfo.MOK) {
                                //                                Log.v("faceSimilar.getScore()", faceSimilar.getScore() + "");
                                if (faceSimilar.getScore() > 0.65f) { //签到成功
                                    signInSuccess(staff);
                                }
                            }
                            compareFaceFeature.setFeatureData(staff.getStaffFaceThreeFeatureData());
                            if (ZffApplication.getFaceEngine().compareFaceFeature(currentFaceFeature, compareFaceFeature, faceSimilar) == ErrorInfo.MOK) {
                                //                                Log.v("faceSimilar.getScore()", faceSimilar.getScore() + "");
                                if (faceSimilar.getScore() > 0.65f) { //签到成功
                                    signInSuccess(staff);
                                }
                            }
                        }
                    }
                }
                int faceEndineProcess = ZffApplication.getFaceEngine().process(data, previewSize.width, previewSize.height, FaceEngine.CP_PAF_NV21, faceInfos, FaceEngine.ASF_AGE | FaceEngine.ASF_GENDER | FaceEngine.ASF_FACE3DANGLE | FaceEngine.ASF_LIVENESS);
                if (faceEndineProcess == ErrorInfo.MOK) {
                    List<AgeInfo> ageInfos = new ArrayList<AgeInfo>();
                    int faceEndineAge = ZffApplication.getFaceEngine().getAge(ageInfos);
                    if (faceEndineAge == ErrorInfo.MOK) {
                        for (AgeInfo ageInfo : ageInfos) {
                            ageValue = ageInfo.getAge();
                            if (ageInfo.getAge() == AgeInfo.UNKNOWN_AGE) {
                                age.setText("");
                            } else if (ageValue > -1 && ageValue < 45) {
                                age.setText(ageValue + "岁");
                            } else {
                                age.setText("");
                            }
                        }
                    }
                    List<GenderInfo> genderInfos = new ArrayList<GenderInfo>();
                    int faceEndineGender = ZffApplication.getFaceEngine().getGender(genderInfos);
                    if (faceEndineGender == ErrorInfo.MOK) {
                        for (GenderInfo genderInfo : genderInfos) {
                            if (genderInfo.getGender() == GenderInfo.FEMALE) {
                                if (ageValue < 11) {
                                    gender.setText("小可爱");
                                } else if (ageValue >= 11 && ageValue < 15) {
                                    gender.setText("豆蔻年华");
                                } else if (ageValue >= 15 && ageValue < 25) {
                                    gender.setText("美少女");
                                } else if (ageValue >= 25 && ageValue < 30) {
                                    gender.setText("花房姑娘");
                                } else if (ageValue >= 30 && ageValue < 35) {
                                    gender.setText("仙女");
                                } else if (ageValue >= 35 && ageValue < 45) {
                                    gender.setText("御姐"); //半老徐娘
                                } else {
                                    gender.setText("");
                                }
                                //                                else if (ageValue >= 45 && ageValue < 60) {
                                //                                    gender.setText("阿姨，求介绍对象！");
                                //                                } else if (ageValue >= 60) {
                                //                                    gender.setText("奶奶，你保养的真好，简直就是鹤发童颜啊！");
                                //                                }
                            } else if (genderInfo.getGender() == GenderInfo.MALE) {
                                if (ageValue < 11) {
                                    gender.setText("少年");
                                } else if (ageValue >= 11 && ageValue < 15) {
                                    gender.setText("帅小伙");
                                } else if (ageValue >= 15 && ageValue < 25) {
                                    gender.setText("小鲜肉！");
                                } else if (ageValue >= 25 && ageValue < 30) {
                                    gender.setText("哇！帅哥！");
                                } else if (ageValue >= 30 && ageValue < 40) {
                                    gender.setText("三十而立，四十不惑。");
                                } else if (ageValue >= 40 && ageValue < 45) {
                                    gender.setText("身体困了吧？大叔！"); //半老徐娘
                                } else {
                                    gender.setText("");
                                }
                                //                                else if (ageValue >= 45 && ageValue < 60) {
                                //                                    gender.setText("知天命，入花甲。");
                                //                                } else if (ageValue >= 60 && ageValue < 75) {
                                //                                    gender.setText("爷爷，一看你就是老而益壮！");
                                //                                } else if (ageValue >= 75) {
                                //                                    gender.setText("爷爷，你是否已饱经风霜？"); //饱经风霜//老气横秋//老而益壮
                                //                                }
                            } else {
                                gender.setText("");
                            }
                        }
                    }
                    List<LivenessInfo> livenessInfos = new ArrayList<LivenessInfo>();
                    int faceEndineLiveness = ZffApplication.getFaceEngine().getLiveness(livenessInfos);
                    if (faceEndineLiveness == ErrorInfo.MOK) {
                        for (LivenessInfo livenessInfoTemp : livenessInfos) {
                            if (livenessInfoTemp.getLiveness() == LivenessInfo.ALIVE) {
                                //                                liveness.setText("嗯，一看你就是能量慢慢，充满活力。");
                                livenessInfo = "ALIVE";
                                liveness.setText("");
                            } else if (livenessInfoTemp.getLiveness() == LivenessInfo.NOT_ALIVE) {
                                //                                liveness.setText("一动不动是王八！");
                                if ("NOT_ALIVE".equals(livenessInfo)) {
                                    liveness.setText("照片不能代替人脸来签到哦！");
                                }
                                livenessInfo = "NOT_ALIVE";
                            } else if (livenessInfoTemp.getLiveness() == LivenessInfo.FACE_NUM_MORE_THAN_ONE) {
                                if ("FACE_NUM_MORE_THAN_ONE".equals(livenessInfo)) {
                                    liveness.setText("这么多人，一个一个来好吗？");
                                }
                                livenessInfo = "FACE_NUM_MORE_THAN_ONE";
                            } else {
                                livenessInfo = "UNKNOW";
                                liveness.setText("");
                            }
                        }
                    }
                }
            }
        }
    };
    private SurfaceHolder.Callback surfaceHolderCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) { //开始预览
            try {
                if (camera == null) {
                    Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
                    for (int cameraIndex = 0; cameraIndex < Camera.getNumberOfCameras(); cameraIndex++) {
                        Camera.getCameraInfo(cameraIndex, cameraInfo);
                        int rotation = SignInActivity.this.getWindowManager().getDefaultDisplay().getRotation();
                        if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                            frontCameraId = cameraIndex;
                            int degrees = 0;
                            switch (rotation) {
                                case Surface.ROTATION_0:
                                    degrees = 0;
                                    break;
                                case Surface.ROTATION_90:
                                    degrees = 90;
                                    break;
                                case Surface.ROTATION_180:
                                    degrees = 180;
                                    break;
                                case Surface.ROTATION_270:
                                    degrees = 270;
                                    break;
                            }
                            frontCameraDegrees = (cameraInfo.orientation + degrees) % 360;
                            frontCameraDegrees = (360 - frontCameraDegrees) % 360;  // compensate the mirror
                            //                            camera = Camera.open(frontCameraId); //前置摄像头
                            //                            camera.setDisplayOrientation(270);   //让相机旋转270度
                        } else if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                            backCameraId = cameraIndex;
                            int degrees = 0;
                            switch (rotation) {
                                case Surface.ROTATION_0:
                                    degrees = 0;
                                    break;
                                case Surface.ROTATION_90:
                                    degrees = 90;
                                    break;
                                case Surface.ROTATION_180:
                                    degrees = 180;
                                    break;
                                case Surface.ROTATION_270:
                                    degrees = 270;
                                    break;
                            }
                            backCameraDegrees = (cameraInfo.orientation - degrees + 360) % 360;
                            //                            camera = Camera.open(backCameraId); //后置摄像头
                            //                            camera.setDisplayOrientation(90);   //让相机旋转90度
                        }
                    }
//                    currentCameraID = backCameraId;
                    currentCameraID = frontCameraId;
                    camera = Camera.open(currentCameraID);
                    camera.setDisplayOrientation(backCameraDegrees);   //让相机旋转90度
                }
                camera.setPreviewDisplay(surfaceView.getHolder());
                camera.setPreviewCallback(previewCallback);
                camera.startPreview();
                //                if (camera != null) {
                //                    camera.unlock(); //从Android 4.0 (API 14)开始, Camera.lock() 和 Camera.unlock() 的调用已经被自动管理了。
                //                    if (mediaRecorder == null) {
                //                        mediaRecorder = new MediaRecorder();
                //                    }
                //                    mediaRecorder.setCamera(camera);
                //                    mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER); //设置音频源
                //                    mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA); //设置视频源
                //                    mediaRecorder.setProfile(CamcorderProfile.get(cameraId, CamcorderProfile.QUALITY_HIGH));
                //                    mediaRecorder.setOutputFile(FileUtil.getNewFile(SignInActivity.this, "video", "SignInVideo.mp4"));
                //                    mediaRecorder.setPreviewDisplay(surfaceView.getHolder().getSurface());
                //                    mediaRecorder.prepare();
                //                    mediaRecorder.start();
                //                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            try {
                //            if (mediaRecorder != null) {
                //                mediaRecorder.stop();
                //                mediaRecorder.reset();
                //                mediaRecorder.release();
                //                mediaRecorder = null;
                //            }
                if (camera != null) {
                    //                camera.lock(); //从Android 4.0 (API 14)开始, Camera.lock() 和 Camera.unlock() 的调用已经被自动管理了。
                    camera.setPreviewCallback(null);
                    camera.setPreviewDisplay(null);
                    camera.stopPreview();
                    camera.release();
                    camera = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (checkCamera() && faceEngineIsInit()) {
            age = findViewById(R.id.age);
            gender = findViewById(R.id.gender);
            liveness = findViewById(R.id.liveness);
            surfaceView = findViewById(R.id.surfaceView);
            surfaceView.getHolder().addCallback(surfaceHolderCallback);
            signInSuccess = findViewById(R.id.signInSuccess);
            signInSuccessLinear = findViewById(R.id.signInSuccessLinear);
            staffId = findViewById(R.id.staffId);
            staffName = findViewById(R.id.staffName);
            signInTime = findViewById(R.id.signInTime);
            signInSuccessT = findViewById(R.id.signInSuccessT);
        } else {
            Toast.makeText(this, "抱歉，摄像头不可用", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.autorenew:
                try {
                    age.setText("");
                    gender.setText("");
                    liveness.setText("");
                    if (camera != null) {
                        //                camera.lock(); //从Android 4.0 (API 14)开始, Camera.lock() 和 Camera.unlock() 的调用已经被自动管理了。
                        camera.stopPreview();
                        camera.setPreviewCallback(null);
                        camera.setPreviewDisplay(null);
                        camera.release();
                        camera = null;
                    }
                    if (currentCameraID == frontCameraId) {
                        currentCameraID = backCameraId;
                        camera = Camera.open(currentCameraID);
                        camera.setDisplayOrientation(backCameraDegrees);   //让相机旋转一定度数
                        camera.setPreviewDisplay(surfaceView.getHolder());
                        camera.setPreviewCallback(previewCallback);
                        camera.startPreview();
                    } else if (currentCameraID == backCameraId) {
                        currentCameraID = frontCameraId;
                        camera = Camera.open(currentCameraID);
                        camera.setDisplayOrientation(frontCameraDegrees);   //让相机旋转一定度数
                        camera.setPreviewDisplay(surfaceView.getHolder());
                        camera.setPreviewCallback(previewCallback);
                        camera.startPreview();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
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

    /**
     * context - 上下文对象
     * detectMode - 检测模式，支持视频模式(ASF_DETECT_MODE_VIDEO)和图像模式(ASF_DETECT_MODE_IMAGE)
     * detectFaceOrientPriority - 人脸检测方向的优先级，支持仅0度(ASF_OP_0_ONLY)，仅90度(ASF_OP_90_ONLY)，仅180度(ASF_OP_180_ONLY)，仅270度(ASF_OP_270_ONLY)，多方向检测(ASF_OP_0_HIGHER_EXT)
     * detectFaceScaleVal - 人脸相对于所在图片的长边的占比，在视频模式(ASF_DETECT_MODE_VIDEO)下有效值范围[2，16]，在图像模式(ASF_DETECT_MODE_IMAGE)下有效值范围[2，32]
     * detectFaceMaxNum - 引擎最多能检测出的人脸数，有效值范围[1,50]
     * combinedMask - 初始化引擎功能组合，可以是ASF_NONE、ASF_FACE_DETECT、ASF_FACE_RECOGNITION、ASF_AGE、ASF_GENDER、ASF_FACE3DANGLE、ASF_LIVENESS中的单个或者多个，用 | 运算符拼接
     *
     * @return FaceEngine是否初始化成功
     */
    private boolean faceEngineIsInit() {
        int faceEngineInit = ZffApplication.getFaceEngine().init(this, DetectMode.ASF_DETECT_MODE_VIDEO, DetectFaceOrientPriority.ASF_OP_ALL_OUT, 16, 1, FaceEngine.ASF_FACE_DETECT | FaceEngine.ASF_FACE_RECOGNITION | FaceEngine.ASF_AGE | FaceEngine.ASF_GENDER | FaceEngine.ASF_FACE3DANGLE | FaceEngine.ASF_LIVENESS | FaceEngine.ASF_IR_LIVENESS);
        Log.v("faceEngineInit", faceEngineInit + "");
        return faceEngineInit == ErrorInfo.MOK;
    }


    private synchronized void signInSuccess(Staff staff) {
        try {
            if ("ALIVE".equals(livenessInfo)) {
                QueryBuilder<Attendance> queryBuilder = attendanceDao.queryBuilder();
                queryBuilder.where(AttendanceDao.Properties.StaffId.eq(staff.getStaffId()), AttendanceDao.Properties.StaffName.eq(staff.getStaffName()), AttendanceDao.Properties.StaffDepartment.eq(staff.getStaffDepartment()), queryBuilder.or(AttendanceDao.Properties.SignInTime.gt(today), AttendanceDao.Properties.SignOutTime.gt(today)));
                List<Attendance> attendances = queryBuilder.list();
                if (attendances != null && attendances.size() > 0) {
                    Attendance attendance = attendances.get(0);
                    if (attendances.get(0).getSignInTime() > 1000) {
                        signInSuccessUI(attendance, "你已经签到过了");
                    } else {
                        attendance.setSignInTime(System.currentTimeMillis());
                        attendanceDao.update(attendance);
                        signInSuccessUI(attendance, "恭喜你，上班签到成功。");
                    }
                } else {
                    Cursor cursor = null;
                    long attendanceId = 0;
                    try {
                        cursor = ZffApplication.getDaoSession(this).getDatabase().rawQuery("select max(ATTENDANCE_ID) MAX_ATTENDANCE_ID from ATTENDANCE", null);
                        while (cursor.moveToNext()) {
                            attendanceId = cursor.getLong(cursor.getColumnIndex("MAX_ATTENDANCE_ID")) + 1;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (cursor != null) {
                            cursor.close();
                        }
                    }
                    Attendance attendance = new Attendance(attendanceId, staff.getStaffId(), staff.getStaffName(), staff.getStaffDepartment(), System.currentTimeMillis(), 0L);
                    attendanceDao.insert(attendance);
                    signInSuccessUI(attendance, "恭喜你，上班签到成功。");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private synchronized void signInSuccessUI(final Attendance attendance, final String result) {
        //        if (!"".equals(age.getText().toString()) && !"".equals(gender.getText().toString()) && !"".equals(liveness.getText().toString())) {
        //        signInSuccess.setVisibility(View.VISIBLE);
        //            工号……姓名……时间……签到类型
        signInSuccessT.setText(result);
        staffId.setText(attendance.getStaffId() + "");
        staffName.setText(attendance.getStaffName());
        signInTime.setText(simpleDateFormat.format(attendance.getSignInTime()));
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            signInSuccessLinear.setVisibility(View.VISIBLE);
                        }
                    });
                    Thread.sleep(2000);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            signInSuccessLinear.setVisibility(View.GONE);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
        //        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (camera != null) {
                //                camera.lock(); //从Android 4.0 (API 14)开始, Camera.lock() 和 Camera.unlock() 的调用已经被自动管理了。
                camera.setPreviewCallback(null);
                camera.setPreviewDisplay(null);
                camera.stopPreview();
                camera.release();
                camera = null;
            }
            //调用FaceEngine的unInit方法销毁引擎。在init成功后如不unInit会导致内存泄漏。
            ZffApplication.getFaceEngine().unInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {
            if (camera != null) {
                //                camera.lock(); //从Android 4.0 (API 14)开始, Camera.lock() 和 Camera.unlock() 的调用已经被自动管理了。
                camera.setPreviewCallback(null);
                camera.setPreviewDisplay(null);
                camera.stopPreview();
                camera.release();
                camera = null;
            }
            //调用FaceEngine的unInit方法销毁引擎。在init成功后如不unInit会导致内存泄漏。
            ZffApplication.getFaceEngine().unInit();
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
