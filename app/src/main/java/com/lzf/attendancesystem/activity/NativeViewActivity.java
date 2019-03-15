package com.lzf.attendancesystem.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.lzf.attendancesystem.R;
import com.lzf.attendancesystem.bean.Attendance;
import com.lzf.attendancesystem.util.ReusableAdapter;

import java.util.ArrayList;
import java.util.List;

public class NativeViewActivity extends AppCompatActivity {
    private ReusableAdapter<Attendance> reusableAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native_view);
        ListView attendanceList = findViewById(R.id.attendanceList);
        List<Attendance> attendances = new ArrayList<Attendance>();
        attendances.add(new Attendance(1, 1, "MJCodder", "2019年3月01日 08:30:00", "2019年3月07日 17:29:00"));
        reusableAdapter = new ReusableAdapter<Attendance>(attendances, R.layout.item_native_view) {
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
