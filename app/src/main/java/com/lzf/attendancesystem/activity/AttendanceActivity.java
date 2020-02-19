package com.lzf.attendancesystem.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import android.widget.Toast;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.lzf.attendancesystem.R;
import com.lzf.attendancesystem.ZffApplication;
import com.lzf.attendancesystem.bean.Attendance;
import com.lzf.attendancesystem.bean.AttendanceAddress;
import com.lzf.attendancesystem.bean.AttendanceAddressDao;
import com.lzf.attendancesystem.bean.AttendanceDao;
import com.lzf.attendancesystem.util.ReusableAdapter;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

public class AttendanceActivity extends AppCompatActivity {
    private AttendanceDao attendanceDao = ZffApplication.getDaoSession(this).getAttendanceDao();
    private ReusableAdapter<Attendance> signInAdapter;
    private ReusableAdapter<Attendance> signOutAdapter;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
    private long today = System.currentTimeMillis() / (1000 * 3600 * 24) * (1000 * 3600 * 24) - TimeZone.getDefault().getRawOffset();//今天零点零分零秒的毫秒数

    private List<AttendanceAddress> attendanceAddresses = ZffApplication.getDaoSession(this).getAttendanceAddressDao().queryBuilder().orderAsc(AttendanceAddressDao.Properties.AttendanceAddressId).list();
    private AMapLocationClient aMapLocationClient;
    private LatLng currentAddress = null;
    //异步获取定位结果
    private AMapLocationListener aMapLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if (aMapLocation != null) {
                if (aMapLocation.getErrorCode() == 0) {
                    //可在其中解析amapLocation获取相应内容。
                    currentAddress = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                } else {
                    //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                    Toast.makeText(AttendanceActivity.this, "location Error, ErrCode:"
                            + aMapLocation.getErrorCode() + ", errInfo:"
                            + aMapLocation.getErrorInfo(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);


        //声明AMapLocationClient类对象//初始化定位
        aMapLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        aMapLocationClient.setLocationListener(aMapLocationListener);
        AMapLocationClientOption aMapLocationClientOption = new AMapLocationClientOption();
        //设置定位场景，目前支持三种场景（签到、出行、运动，默认无场景）
        aMapLocationClientOption.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.SignIn);
        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        aMapLocationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        /**
         * 获取最近3s内精度最高的一次定位结果：
         * 设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会，默认为false。
         */
        aMapLocationClientOption.setOnceLocationLatest(true);
        //设置是否返回地址信息（默认返回地址信息）
        aMapLocationClientOption.setNeedAddress(true);
        //设置是否允许模拟位置,默认为true，允许模拟位置
        aMapLocationClientOption.setMockEnable(true);
        //开启缓存机制
        aMapLocationClientOption.setLocationCacheEnable(true);
        if (null != aMapLocationClientOption) {
            aMapLocationClient.setLocationOption(aMapLocationClientOption);
            //设置场景模式后最好调用一次stop，再调用start以保证场景模式生效
            aMapLocationClient.stopLocation();
            aMapLocationClient.startLocation(); //启动定位
        }

        //        Log.v("today", today + "");

        ListView signInList = findViewById(R.id.signInList);
        signInAdapter = new ReusableAdapter<Attendance>(attendanceDao.queryBuilder().where(AttendanceDao.Properties.SignInTime.gt(today)).orderDesc(AttendanceDao.Properties.SignInTime).list(), R.layout.item_sign) {
            @Override
            public void bindView(ViewHolder holder, Attendance obj) {
                holder.setText(R.id.staffId, obj.getStaffId() + "");
                holder.setText(R.id.staffName, obj.getStaffName());
                holder.setText(R.id.staffDepartment, obj.getStaffDepartment());
                if (attendanceAddresses != null && attendanceAddresses.size() > 0) {
                    LatLng attendanceAddress = new LatLng(attendanceAddresses.get(0).getLatitude(), attendanceAddresses.get(0).getLongitude());
                    if (AMapUtils.calculateLineDistance(attendanceAddress, new LatLng(obj.getSignInLatitude(), obj.getSignInLongitude())) < 1500) {
                        holder.setText(R.id.sign, simpleDateFormat.format(obj.getSignInTime()) + "（正常签到）");
                    } else {
                        holder.setText(R.id.sign, simpleDateFormat.format(obj.getSignInTime()) + "（外出签到）");
                    }
                } else {
                    holder.setText(R.id.sign, simpleDateFormat.format(obj.getSignInTime()));
                }
            }
        };
        signInList.setAdapter(signInAdapter);


        ListView signOutList = findViewById(R.id.signOutList);
        signOutAdapter = new ReusableAdapter<Attendance>(attendanceDao.queryBuilder().where(AttendanceDao.Properties.SignOutTime.gt(today)).orderDesc(AttendanceDao.Properties.SignOutTime).list(), R.layout.item_sign) {
            @Override
            public void bindView(ViewHolder holder, Attendance obj) {
                holder.setText(R.id.staffId, obj.getStaffId() + "");
                holder.setText(R.id.staffName, obj.getStaffName());
                holder.setText(R.id.staffDepartment, obj.getStaffDepartment());
                if (attendanceAddresses != null && attendanceAddresses.size() > 0) {
                    LatLng attendanceAddress = new LatLng(attendanceAddresses.get(0).getLatitude(), attendanceAddresses.get(0).getLongitude());
                    if (AMapUtils.calculateLineDistance(attendanceAddress, new LatLng(obj.getSignInLatitude(), obj.getSignInLongitude())) < 1500) {
                        holder.setText(R.id.sign, simpleDateFormat.format(obj.getSignOutTime()) + "（正常签退）");
                    } else {
                        holder.setText(R.id.sign, simpleDateFormat.format(obj.getSignOutTime()) + "（外出签退）");
                    }
                } else {
                    holder.setText(R.id.sign, simpleDateFormat.format(obj.getSignOutTime()));
                }
            }
        };
        signOutList.setAdapter(signOutAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        attendanceDao.detachAll();
        signInAdapter.updateAll(attendanceDao.queryBuilder().where(AttendanceDao.Properties.SignInTime.gt(today)).orderDesc(AttendanceDao.Properties.SignInTime).list());
        attendanceDao.detachAll();
        signOutAdapter.updateAll(attendanceDao.queryBuilder().where(AttendanceDao.Properties.SignOutTime.gt(today)).orderDesc(AttendanceDao.Properties.SignOutTime).list());
    }

    public void onClick(final View view) {
        if (attendanceAddresses != null && attendanceAddresses.size() > 0 && currentAddress != null) {
            LatLng attendanceAddress = new LatLng(attendanceAddresses.get(0).getLatitude(), attendanceAddresses.get(0).getLongitude());
            if (AMapUtils.calculateLineDistance(attendanceAddress, currentAddress) < 1500) {
                clickInternal(view.getId());
            } else {
                AlertDialog alertDialog = new AlertDialog.Builder(this).setIcon(R.mipmap.ic_launcher)
                        .setTitle("系统提示：")
                        .setMessage("不在考勤地址范围内，您确定要打卡么？")
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                aMapLocationClient.stopLocation();
                                aMapLocationClient.startLocation(); //启动定位
                            }
                        })
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                clickInternal(view.getId());
                            }
                        }).create();             //创建AlertDialog对象
                alertDialog.show();                    //显示对话框
            }
        } else {
            clickInternal(view.getId());
        }
    }

    private void clickInternal(int id) {
        try {
            Intent intent = new Intent();
            Log.v("intent", intent + "");
            switch (id) {
                case R.id.signIn:
                    intent.setClass(this, SignInActivity.class);
                    intent.putExtra("latitude", currentAddress.latitude);
                    intent.putExtra("longitude", currentAddress.longitude);
                    startActivity(intent);
                    break;
                case R.id.signOut:
                    intent.setClass(this, SignOutActivity.class);
                    intent.putExtra("latitude", currentAddress.latitude);
                    intent.putExtra("longitude", currentAddress.longitude);
                    startActivity(intent);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (aMapLocationClient != null) {
            aMapLocationClient.stopLocation();//停止定位后，本地定位服务并不会被销毁
            aMapLocationClient.onDestroy();//销毁定位客户端，同时销毁本地定位服务。
        }
    }
}
