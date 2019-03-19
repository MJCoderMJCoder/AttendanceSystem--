package com.lzf.attendancesystem.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.lzf.attendancesystem.R;
import com.lzf.attendancesystem.ZffApplication;
import com.lzf.attendancesystem.bean.Admin;
import com.lzf.attendancesystem.bean.AdminDao;

import java.util.List;

public class AdminMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<Admin> admins = ZffApplication.getDaoSession(this).getAdminDao().queryBuilder().orderAsc(AdminDao.Properties.AdminId).list();
        if (admins != null && admins.size() > 0) {
            Admin admin = admins.get(0);
            ((TextView) findViewById(R.id.topTxt)).setText(admin.getAdminName() + "管理员欢迎您！");
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.systemSetTime:
                Intent intent = new Intent();
                intent.setAction("android.settings.DATE_SETTINGS");
                startActivity(intent);
                break;
            case R.id.staffManage:
                startActivity(new Intent(this, StaffManageActivity.class));
                break;
            case R.id.attendanceDataExport:
                startActivity(new Intent(this, AttendanceDataExportActivity.class));
                break;
            case R.id.attendanceTimeManage:
                break;
            case R.id.modifyAccountInfo:
                startActivity(new Intent(this, ModifyAccountActivity.class));
                break;
            default:
                break;
        }
    }
}
