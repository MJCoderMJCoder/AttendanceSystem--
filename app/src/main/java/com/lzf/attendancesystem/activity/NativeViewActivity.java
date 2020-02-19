package com.lzf.attendancesystem.activity;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.lzf.attendancesystem.R;
import com.lzf.attendancesystem.ZffApplication;
import com.lzf.attendancesystem.bean.Attendance;
import com.lzf.attendancesystem.bean.AttendanceAddress;
import com.lzf.attendancesystem.bean.AttendanceAddressDao;
import com.lzf.attendancesystem.bean.AttendanceDao;
import com.lzf.attendancesystem.util.ReusableAdapter;

import org.greenrobot.greendao.query.QueryBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class NativeViewActivity extends AppCompatActivity {
    private AttendanceDao attendanceDao = ZffApplication.getDaoSession(this).getAttendanceDao();
    private ReusableAdapter<Attendance> reusableAdapter;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
    private List<AttendanceAddress> attendanceAddresses = ZffApplication.getDaoSession(this).getAttendanceAddressDao().queryBuilder().orderAsc(AttendanceAddressDao.Properties.AttendanceAddressId).list();

    private CheckBox staffIDCheck;
    private CheckBox startTimeCheck;
    private CheckBox endTimeCheck;
    private EditText staffIDEdit;
    private DatePicker startDatePicker;
    private DatePicker endDatePicker;

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native_view);
        ListView attendanceList = findViewById(R.id.attendanceList);
        reusableAdapter = new ReusableAdapter<Attendance>(attendanceDao.queryBuilder().orderDesc(AttendanceDao.Properties.SignInTime).list(), R.layout.item_native_view) {
            @Override
            public void bindView(ViewHolder holder, Attendance obj) {
                holder.setText(R.id.staffId, obj.getStaffId() + "");
                holder.setText(R.id.staffName, obj.getStaffName());
                holder.setText(R.id.staffDepartment, obj.getStaffDepartment());
                if (obj.getSignInTime() > 1000) {
                    if (attendanceAddresses != null && attendanceAddresses.size() > 0) {
                        LatLng attendanceAddress = new LatLng(attendanceAddresses.get(0).getLatitude(), attendanceAddresses.get(0).getLongitude());
                        if (AMapUtils.calculateLineDistance(attendanceAddress, new LatLng(obj.getSignInLatitude(), obj.getSignInLongitude())) < 1500) {
                            holder.setText(R.id.signInTime, simpleDateFormat.format(obj.getSignInTime()) + "（正常签到）");
                        } else {
                            holder.setText(R.id.signInTime, simpleDateFormat.format(obj.getSignInTime()) + "（外出签到）");
                        }
                    } else {
                        holder.setText(R.id.sign, simpleDateFormat.format(obj.getSignInTime()));
                    }
                } else {
                    holder.setText(R.id.signInTime, "");
                }
                if (obj.getSignOutTime() > 1000) {
                    if (attendanceAddresses != null && attendanceAddresses.size() > 0) {
                        LatLng attendanceAddress = new LatLng(attendanceAddresses.get(0).getLatitude(), attendanceAddresses.get(0).getLongitude());
                        if (AMapUtils.calculateLineDistance(attendanceAddress, new LatLng(obj.getSignInLatitude(), obj.getSignInLongitude())) < 1500) {
                            holder.setText(R.id.signOutTime, simpleDateFormat.format(obj.getSignInTime()) + "（正常签退）");
                        } else {
                            holder.setText(R.id.signOutTime, simpleDateFormat.format(obj.getSignInTime()) + "（外出签退）");
                        }
                    } else {
                        holder.setText(R.id.signOutTime, simpleDateFormat.format(obj.getSignInTime()));
                    }
                } else {
                    holder.setText(R.id.signOutTime, "");
                }
            }
        };
        attendanceList.setAdapter(reusableAdapter);

        staffIDCheck = findViewById(R.id.staffIDCheck);
        startTimeCheck = findViewById(R.id.startTimeCheck);
        endTimeCheck = findViewById(R.id.endTimeCheck);
        staffIDEdit = findViewById(R.id.staffIDEdit);
        startDatePicker = findViewById(R.id.startDatePicker);
        endDatePicker = findViewById(R.id.endDatePicker);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.query:
                try {
                    String staffId = staffIDEdit.getText().toString().trim();
                    long startDate = simpleDateFormat.parse(startDatePicker.getYear() + "年" + (startDatePicker.getMonth() + 1) + "月" + startDatePicker.getDayOfMonth() + "日" + " 00:00:00").getTime();
                    long endDate = simpleDateFormat.parse(endDatePicker.getYear() + "年" + (endDatePicker.getMonth() + 1) + "月" + endDatePicker.getDayOfMonth() + "日" + " 23:59:59").getTime();
                    if (endDate > startDate) {
                        QueryBuilder<Attendance> queryBuilder = attendanceDao.queryBuilder();
                        if (staffIDCheck.isChecked()) {
                            if (startTimeCheck.isChecked()) {
                                if (endTimeCheck.isChecked()) {
                                    queryBuilder.where(AttendanceDao.Properties.StaffId.eq(staffId), queryBuilder.or(queryBuilder.and(AttendanceDao.Properties.SignInTime.ge(startDate), AttendanceDao.Properties.SignInTime.le(endDate)), queryBuilder.and(AttendanceDao.Properties.SignOutTime.ge(startDate), AttendanceDao.Properties.SignOutTime.le(endDate))));
                                } else {
                                    queryBuilder.where(AttendanceDao.Properties.StaffId.eq(staffId), queryBuilder.or(AttendanceDao.Properties.SignInTime.ge(startDate), AttendanceDao.Properties.SignOutTime.ge(startDate)));
                                }
                            } else if (endTimeCheck.isChecked()) {
                                queryBuilder.where(AttendanceDao.Properties.StaffId.eq(staffId), queryBuilder.or(AttendanceDao.Properties.SignInTime.le(endDate), AttendanceDao.Properties.SignOutTime.le(endDate)));
                            } else {
                                queryBuilder.where(AttendanceDao.Properties.StaffId.eq(staffId));
                            }
                        } else if (startTimeCheck.isChecked()) {
                            if (endTimeCheck.isChecked()) {
                                queryBuilder.where(queryBuilder.or(queryBuilder.and(AttendanceDao.Properties.SignInTime.ge(startDate), AttendanceDao.Properties.SignInTime.le(endDate)), queryBuilder.and(AttendanceDao.Properties.SignOutTime.ge(startDate), AttendanceDao.Properties.SignOutTime.le(endDate))));
                            } else {
                                queryBuilder.where(queryBuilder.or(AttendanceDao.Properties.SignInTime.ge(startDate), AttendanceDao.Properties.SignOutTime.ge(startDate)));
                            }
                        } else if (endTimeCheck.isChecked()) {
                            queryBuilder.where(queryBuilder.or(AttendanceDao.Properties.SignInTime.le(endDate), AttendanceDao.Properties.SignOutTime.le(endDate)));
                        }
                        reusableAdapter.updateAll(queryBuilder.orderDesc(AttendanceDao.Properties.SignInTime).list());
                    } else {
                        Toast.makeText(this, "起始时间不能大于截止时间", Toast.LENGTH_SHORT).show();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }
}
