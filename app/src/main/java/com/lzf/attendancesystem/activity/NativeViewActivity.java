package com.lzf.attendancesystem.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.lzf.attendancesystem.R;
import com.lzf.attendancesystem.ZffApplication;
import com.lzf.attendancesystem.bean.Attendance;
import com.lzf.attendancesystem.bean.AttendanceDao;
import com.lzf.attendancesystem.util.ReusableAdapter;

public class NativeViewActivity extends AppCompatActivity {
    private AttendanceDao attendanceDao = ZffApplication.getDaoSession(this).getAttendanceDao();
    private ReusableAdapter<Attendance> reusableAdapter;

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
                holder.setText(R.id.signInTime, obj.getSignInTime());
                holder.setText(R.id.signOutTime, obj.getSignOutTime());
            }
        };
        attendanceList.setAdapter(reusableAdapter);
    }
}
