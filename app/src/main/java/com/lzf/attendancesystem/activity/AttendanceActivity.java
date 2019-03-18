package com.lzf.attendancesystem.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.lzf.attendancesystem.R;
import com.lzf.attendancesystem.ZffApplication;
import com.lzf.attendancesystem.bean.Attendance;
import com.lzf.attendancesystem.bean.AttendanceDao;
import com.lzf.attendancesystem.util.ReusableAdapter;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class AttendanceActivity extends AppCompatActivity {
    private AttendanceDao attendanceDao = ZffApplication.getDaoSession(this).getAttendanceDao();
    private ReusableAdapter<Attendance> signInAdapter;
    private ReusableAdapter<Attendance> signOutAdapter;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
    private long today = System.currentTimeMillis() / (1000 * 3600 * 24) * (1000 * 3600 * 24) - TimeZone.getDefault().getRawOffset();//今天零点零分零秒的毫秒数

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        Log.v("today", today + "");

        ListView signInList = findViewById(R.id.signInList);
        signInAdapter = new ReusableAdapter<Attendance>(attendanceDao.queryBuilder().where(AttendanceDao.Properties.SignInTime.gt(today)).orderDesc(AttendanceDao.Properties.SignInTime).list(), R.layout.item_sign) {
            @Override
            public void bindView(ViewHolder holder, Attendance obj) {
                holder.setText(R.id.staffId, obj.getStaffId() + "");
                holder.setText(R.id.staffName, obj.getStaffName());
                holder.setText(R.id.staffDepartment, obj.getStaffDepartment());
                holder.setText(R.id.sign, simpleDateFormat.format(obj.getSignInTime()));
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
                holder.setText(R.id.sign, simpleDateFormat.format(obj.getSignOutTime()));
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

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.signIn:
                startActivity(new Intent(this, SignInActivity.class));
                break;
            case R.id.signOut:
                startActivity(new Intent(this, SignOutActivity.class));
                break;
            default:
                break;
        }
    }
}
