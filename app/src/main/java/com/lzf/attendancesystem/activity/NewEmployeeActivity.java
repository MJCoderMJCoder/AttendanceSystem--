package com.lzf.attendancesystem.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.arcsoft.face.ErrorInfo;
import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.FaceFeature;
import com.arcsoft.face.FaceInfo;
import com.arcsoft.face.enums.DetectFaceOrientPriority;
import com.arcsoft.face.enums.DetectMode;
import com.arcsoft.imageutil.ArcSoftImageFormat;
import com.arcsoft.imageutil.ArcSoftImageUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lzf.attendancesystem.R;
import com.lzf.attendancesystem.ZffApplication;
import com.lzf.attendancesystem.bean.Staff;
import com.lzf.attendancesystem.util.ArcFaceUtil;
import com.lzf.attendancesystem.util.BitmapUtil;
import com.lzf.attendancesystem.util.FileUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewEmployeeActivity extends AppCompatActivity {
    private long staffId;
    private String staffName;
    private String staffGender = "女";
    private String staffDepartment;
    private File staffFaceOne;
    private byte[] staffFaceOneFeatureData;
    private File staffFaceTwo;
    private byte[] staffFaceTwoFeatureData;
    private File staffFaceThree;
    private byte[] staffFaceThreeFeatureData;
    private int whichImg = -6003;

    private final int IMAGE_REQUEST_CODE = 6003;  //获取相册图片
    private final int CAMERA_REQUEST_CODE = 6004; //拍照

    private EditText staffNameEdit;
    private RadioGroup radioGroup;
    private EditText staffDepartmentEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_employee);
        if (faceEngineIsInit()) {
            Cursor cursor = null;
            try {
                cursor = ZffApplication.getDaoSession(this).getDatabase().rawQuery("select max(STAFF_ID) MAX_STAFF_ID from STAFF", null);
                while (cursor.moveToNext()) {
                    staffId = cursor.getLong(cursor.getColumnIndex("MAX_STAFF_ID")) + 1;
                    TextView staffIdTxt = findViewById(R.id.staffIdTxt);
                    staffIdTxt.setText(staffId + "");
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
            staffNameEdit = findViewById(R.id.staffNameEdit);
            radioGroup = findViewById(R.id.radioGroup);
            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    staffGender = ((RadioButton) findViewById(checkedId)).getText().toString().trim();
                }
            });
            staffDepartmentEdit = findViewById(R.id.staffDepartmentEdit);
        } else {
            Toast.makeText(this, "抱歉，摄像头不可用", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.submit:
                try {
                    staffName = staffNameEdit.getText().toString();
                    if ("".equals(staffName)) {
                        Toast.makeText(this, "请输入员工姓名", Toast.LENGTH_SHORT).show();
                    } else {
                        staffDepartment = staffDepartmentEdit.getText().toString();
                        if ("".equals(staffDepartment)) {
                            Toast.makeText(this, "请输入部门名称", Toast.LENGTH_SHORT).show();
                        } else {
                            if (staffFaceOne == null || staffFaceOneFeatureData == null || staffFaceTwo == null || staffFaceThreeFeatureData == null || staffFaceThree == null || staffFaceThreeFeatureData == null) {
                                Toast.makeText(this, "请上传三张包含清晰脸部的图片", Toast.LENGTH_SHORT).show();
                            } else {
                                Staff staff = new Staff(staffId, staffName, staffGender, staffDepartment, staffFaceOne.getAbsolutePath(), staffFaceOneFeatureData, staffFaceTwo.getAbsolutePath(), staffFaceTwoFeatureData, staffFaceThree.getAbsolutePath(), staffFaceThreeFeatureData);
                                if (ZffApplication.getDaoSession(this).getStaffDao().insert(staff) > 0) {
                                    finish();
                                } else {
                                    Toast.makeText(this, "新增员工失败", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "新增员工失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.oneImg:
                whichImg = R.id.oneImg;
                choosePicture();
                break;
            case R.id.twoImg:
                whichImg = R.id.twoImg;
                choosePicture();
                break;
            case R.id.threeImg:
                whichImg = R.id.threeImg;
                choosePicture();
                break;
            default:
                break;
        }
    }

    /**
     * 通过用户点击的那个ImageView视图；判定当前操作的应该是哪个文件；并获取该文件
     *
     * @return
     */
    private File getCurrentFile(FaceFeature faceFeature) {
        switch (whichImg) {
            case R.id.oneImg:
                staffFaceOne = FileUtil.getFile(this, ".image", staffId + "_staffFaceOne.jpg");
                if (faceFeature != null) {
                    staffFaceOneFeatureData = faceFeature.getFeatureData();
                }
                return staffFaceOne;
            case R.id.twoImg:
                staffFaceTwo = FileUtil.getFile(this, ".image", staffId + "_staffFaceTwo.jpg");
                if (faceFeature != null) {
                    staffFaceTwoFeatureData = faceFeature.getFeatureData();
                }
                return staffFaceTwo;
            case R.id.threeImg:
                staffFaceThree = FileUtil.getFile(this, ".image", staffId + "_staffFaceThree.jpg");
                if (faceFeature != null) {
                    staffFaceThreeFeatureData = faceFeature.getFeatureData();
                }
                return staffFaceThree;
            default:
                return null;
        }
    }

    /**
     * 通过用户点击的那个ImageView视图；判定当前操作的应该是哪个文件；并给该文件重新赋值
     *
     * @param file
     */
    private void setCurrentFile(File file) {
        switch (whichImg) {
            case R.id.oneImg:
                staffFaceOne = file;
                break;
            case R.id.twoImg:
                staffFaceTwo = file;
                break;
            case R.id.threeImg:
                staffFaceThree = file;
                break;
            default:
                break;
        }
    }

    public void choosePicture() {
        // 置入一个不设防的VmPolicy：Android 7.0 FileUriExposedException
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }
        try {
            String[] items = new String[]{"选择本地已有图片", "拍摄新照片"};
            new AlertDialog.Builder(this)
                    .setTitle("上传【包含清晰脸部的】图片")
                    .setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0:
                                    //获取相册图片
                                    Intent pickIntent = new Intent(Intent.ACTION_PICK);
                                    pickIntent.setType("image/*");
                                    if ((pickIntent.resolveActivity(getPackageManager()) != null)) {
                                        startActivityForResult(pickIntent, IMAGE_REQUEST_CODE);
                                    } else {
                                        Toast.makeText(NewEmployeeActivity.this, "本地暂无图片", Toast.LENGTH_SHORT).show();
                                    }
                                    break;
                                case 1:
                                    //拍照
                                    Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                    captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getCurrentFile(null)));
                                    startActivityForResult(captureIntent, CAMERA_REQUEST_CODE);
                                    break;
                            }
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            // requestCode：6003；resultCode-1；dataIntent { dat=file:///storage/emulated/0/DCIM/Camera/faceu_20180922152712.jpg typ=image/jpeg }
            Log.v("onActivityResult", "requestCode：" + requestCode + "；resultCode：" + resultCode + "；data：" + data);
            if (resultCode == Activity.RESULT_OK) {
                switch (requestCode) {
                    case CAMERA_REQUEST_CODE:
                        //部分机型（vivo v3）返回时会清除currentImageFile所占的内存空间。（重新走MyApplication导致）
                        Log.v("拍照返回", getCurrentFile(null) + "");
                        break;
                    case IMAGE_REQUEST_CODE:
                        File tempFile = null;
                        Uri selectedImage = data.getData(); // 获取系统返回的照片的Uri
                        tempFile = new File(selectedImage.getPath()); // 获取照片路径
                        if (tempFile == null) {
                            String[] filePathColumn = new String[]{MediaStore.Images.Media.DATA};
                            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);// 从系统表中查询指定Uri对应的照片
                            if (cursor == null) {
                                selectedImage = getXiaoMiUri(data);//解决方案( 解决小米手机上获取图片路径为null的情况)
                                filePathColumn = new String[]{MediaStore.Images.Media.DATA};
                                cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);// 从系统表中查询指定Uri对应的照片
                            }
                            cursor.moveToFirst();
                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            tempFile = new File(cursor.getString(columnIndex)); // 获取照片路径
                            cursor.close();
                        }
                        FileInputStream fis = null;
                        FileOutputStream fos = null;
                        try {
                            //文件复制到sd卡中
                            fis = new FileInputStream(tempFile);
                            fos = new FileOutputStream(getCurrentFile(null));
                            int len = 0;
                            byte[] buffer = new byte[2048];
                            while (-1 != (len = fis.read(buffer))) {
                                fos.write(buffer, 0, len);
                            }
                            fos.flush();
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            //关闭数据流
                            try {
                                if (fos != null)
                                    fos.close();
                                if (fis != null)
                                    fis.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                    default:
                        break;
                }

                /**
                 * data - 输入的图像数据
                 * width - 图像的宽度
                 * height - 图像的高度
                 * format - 图像的颜色空间格式，支持NV21(CP_PAF_NV21)、BGR24(CP_PAF_BGR24)
                 * faceInfoList - 人脸列表，传入后赋值
                 */
                List<FaceInfo> faceInfos = new ArrayList<FaceInfo>();
                // Bitmap bitmap = BitmapFactory.decodeFile(getCurrentFile(null).getAbsolutePath());
                Bitmap bitmap = BitmapUtil.loadBitmap(getCurrentFile(null).getAbsolutePath());
                Log.v("bitmap", bitmap + "");
                if (ZffApplication.getFaceEngine().detectFaces(ArcFaceUtil.bitmapToNv21(bitmap, bitmap.getWidth(), bitmap.getHeight()), bitmap.getWidth(), bitmap.getHeight(), FaceEngine.CP_PAF_NV21, faceInfos) == ErrorInfo.MOK && faceInfos.size() > 0) {
                    Log.v("faceInfos", faceInfos + "");
                    FaceFeature currentFaceFeature = new FaceFeature();
                    int faceEngineExtract = -1;
                    for (FaceInfo faceInfo : faceInfos) {
                        faceEngineExtract = ZffApplication.getFaceEngine().extractFaceFeature(ArcFaceUtil.bitmapToNv21(bitmap, bitmap.getWidth(), bitmap.getHeight()), bitmap.getWidth(), bitmap.getHeight(), FaceEngine.CP_PAF_NV21, faceInfo, currentFaceFeature);
                    }
                    if (faceEngineExtract == ErrorInfo.MOK) {
                        Glide.with(this).load(getCurrentFile(currentFaceFeature))
                                .skipMemoryCache(true) //禁止内存缓存
                                .diskCacheStrategy(DiskCacheStrategy.NONE)//图片缓存策略,这个一般必须有
                                .error(R.drawable.ic_add_a_photo_black_24dp)// 加载图片失败的时候显示的默认图
                                .into(((ImageView) findViewById(whichImg)));
                    } else {
                        setCurrentFile(null);
                        Toast.makeText(this, "抱歉，人脸不清晰，请重新选择。", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    setCurrentFile(null);
                    Toast.makeText(this, "抱歉，未检测到人脸，请重新选择。", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            BitmapUtil.releaseBitmap(getCurrentFile(null).getAbsolutePath());
        }
    }

    /**
     * 解决小米手机上获取图片路径为null的情况
     *
     * @param intent
     * @return
     */
    public Uri getXiaoMiUri(Intent intent) {
        Uri uri = intent.getData();
        String type = intent.getType();
        if (uri.getScheme().equals("file") && (type.contains("image/"))) {
            String path = uri.getEncodedPath();
            if (path != null) {
                path = Uri.decode(path);
                ContentResolver cr = this.getContentResolver();
                StringBuffer buff = new StringBuffer();
                buff.append("(").append(MediaStore.Images.ImageColumns.DATA).append("=").append("'" + path + "'").append(")");
                Cursor cur = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.ImageColumns._ID}, buff.toString(), null, null);
                int index = 0;
                for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
                    index = cur.getColumnIndex(MediaStore.Images.ImageColumns._ID);
                    // set _id value
                    index = cur.getInt(index);
                }
                if (index == 0) {
                    // do nothing
                } else {
                    Uri uri_temp = Uri.parse("content://media/external/images/media/" + index);
                    if (uri_temp != null) {
                        uri = uri_temp;
                    }
                }
            }
        }
        return uri;
    }


    /**
     * context - 上下文对象
     * detectMode - 检测模式，支持视频模式(ASF_DETECT_MODE_VIDEO)和图像模式(ASF_DETECT_MODE_IMAGE)
     * detectFaceOrientPriority - 人脸检测方向的优先级，支持仅0度(ASF_OP_0_ONLY)，仅90度(ASF_OP_90_ONLY)，仅180度(ASF_OP_180_ONLY)，仅270度(ASF_OP_270_ONLY)，多方向检测(ASF_OP_0_HIGHER_EXT)
     * detectFaceScaleVal - 人脸相对于所在图片的长边的占比，在视频模式(ASF_DETECT_MODE_VIDEO)下有效值范围[2，16]VIDEO模式推荐16，在图像模式(ASF_DETECT_MODE_IMAGE)下有效值范围[2，32]IMAGE模式推荐32
     * detectFaceMaxNum - 引擎最多能检测出的人脸数，有效值范围[1,50]
     * combinedMask - 初始化引擎功能组合，可以是ASF_NONE、ASF_FACE_DETECT、ASF_FACE_RECOGNITION、ASF_AGE、ASF_GENDER、ASF_FACE3DANGLE、ASF_LIVENESS中的单个或者多个，用 | 运算符拼接
     *
     * @return FaceEngine是否初始化成功
     */
    private boolean faceEngineIsInit() {
        int faceEngineInit = ZffApplication.getFaceEngine().init(this, DetectMode.ASF_DETECT_MODE_IMAGE, DetectFaceOrientPriority.ASF_OP_ALL_OUT, 32, 1, FaceEngine.ASF_FACE_DETECT | FaceEngine.ASF_FACE_RECOGNITION | FaceEngine.ASF_AGE | FaceEngine.ASF_GENDER | FaceEngine.ASF_FACE3DANGLE | FaceEngine.ASF_LIVENESS | FaceEngine.ASF_IR_LIVENESS);
        Log.v("faceEngineInit", faceEngineInit + "");
        return faceEngineInit == ErrorInfo.MOK;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //调用FaceEngine的unInit方法销毁引擎。在init成功后如不unInit会导致内存泄漏。
        ZffApplication.getFaceEngine().unInit();
    }
}
