package com.lzf.attendancesystem.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.lzf.attendancesystem.R;
import com.lzf.attendancesystem.ZffApplication;
import com.lzf.attendancesystem.bean.Attendance;
import com.lzf.attendancesystem.bean.AttendanceDao;
import com.lzf.attendancesystem.util.ReusableAdapter;

import java.text.SimpleDateFormat;

public class NativeViewActivity extends AppCompatActivity {
    private AttendanceDao attendanceDao = ZffApplication.getDaoSession(this).getAttendanceDao();
    private ReusableAdapter<Attendance> reusableAdapter;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native_view);
        ListView attendanceList = findViewById(R.id.attendanceList);
        reusableAdapter = new ReusableAdapter<Attendance>(attendanceDao.queryBuilder().orderAsc(AttendanceDao.Properties.AttendanceId).list(), R.layout.item_native_view) {
            @Override
            public void bindView(ViewHolder holder, Attendance obj) {
                holder.setText(R.id.staffID, obj.getStaffId() + "");
                holder.setText(R.id.staffName, obj.getStaffName());
                if (obj.getSignInTime() > 1000) {
                    holder.setText(R.id.signInTime, simpleDateFormat.format(obj.getSignInTime()));
                } else {
                    holder.setText(R.id.signInTime, "");
                }
                if (obj.getSignOutTime() > 1000) {
                    holder.setText(R.id.signOutTime, simpleDateFormat.format(obj.getSignOutTime()));
                } else {
                    holder.setText(R.id.signOutTime, "");
                }
            }
        };
        attendanceList.setAdapter(reusableAdapter);
    }
}
