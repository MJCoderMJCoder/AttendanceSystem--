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
        attendances.add(new Attendance(1, "MJCodder", "2019年3月01日 08:30:00", "2019年3月07日 17:29:00"));
        attendances.add(new Attendance(1, "MJCodder", "2019年3月02日 08:30:00", "2019年3月02日 17:30:00"));
        attendances.add(new Attendance(1, "MJCodder", "2019年3月03日 08:16:00", "2019年3月03日 17:30:20"));
        attendances.add(new Attendance(1, "MJCodder", "2019年3月04日 08:30:00", "2019年3月04日 17:30:00"));
        attendances.add(new Attendance(1, "MJCodder", "2019年3月05日 08:30:00", "2019年3月05日 17:30:06"));
        attendances.add(new Attendance(1, "MJCodder", "2019年3月06日 08:30:28", "2019年3月06日 17:30:00"));
        attendances.add(new Attendance(1, "MJCodder", "2019年3月07日 08:30:00", "2019年3月07日 17:30:00"));
        attendances.add(new Attendance(1, "MJCodder", "2019年3月08日 08:30:00", "2019年3月08日 17:30:00"));
        attendances.add(new Attendance(1, "MJCodder", "2019年3月09日 08:30:40", "2019年3月09日 17:30:00"));
        attendances.add(new Attendance(1, "MJCodder", "2019年3月10日 08:30:00", "2019年3月10日 17:30:00"));
        attendances.add(new Attendance(1, "MJCodder", "2019年3月11日 08:33:00", "2019年3月11日 17:30:00"));
        attendances.add(new Attendance(1, "MJCodder", "2019年3月12日 08:30:00", "2019年3月12日 17:30:00"));
        attendances.add(new Attendance(1, "MJCodder", "2019年3月13日 08:30:00", "2019年3月13日 17:30:00"));
        reusableAdapter = new ReusableAdapter<Attendance>(attendances, R.layout.item_native_view) {
            @Override
            public void bindView(ViewHolder holder, Attendance obj) {
                holder.setText(R.id.staffID, obj.getStaffID() + "");
                holder.setText(R.id.staffName, obj.getStaffName());
                holder.setText(R.id.signInTime, obj.getSignInTime());
                holder.setText(R.id.signOutTime, obj.getSignOutTime());
            }
        };
        attendanceList.setAdapter(reusableAdapter);
    }
}
