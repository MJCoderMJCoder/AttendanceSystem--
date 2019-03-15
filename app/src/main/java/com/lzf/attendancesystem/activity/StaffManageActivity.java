package com.lzf.attendancesystem.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;

import com.lzf.attendancesystem.R;
import com.lzf.attendancesystem.ZffApplication;
import com.lzf.attendancesystem.bean.Staff;
import com.lzf.attendancesystem.bean.StaffDao;
import com.lzf.attendancesystem.util.ReusableAdapter;

public class StaffManageActivity extends AppCompatActivity {

    private StaffDao staffDao = ZffApplication.getDaoSession(this).getStaffDao();
    private ReusableAdapter<Staff> reusableAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_manage);
        ListView employeeList = findViewById(R.id.employeeList);
        reusableAdapter = new ReusableAdapter<Staff>(staffDao.queryBuilder().orderAsc(StaffDao.Properties.StaffId).list(), R.layout.item_staff_manage) {
            @Override
            public void bindView(ViewHolder holder, Staff obj) {
                holder.setText(R.id.employeeID, obj.getStaffId() + "");
                holder.setText(R.id.name, obj.getStaffName());
                holder.setText(R.id.gender, obj.getStaffGender());
                holder.setText(R.id.department, obj.getStaffDepartment());
            }
        };
        employeeList.setAdapter(reusableAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //清除单个DAO的标识​​范围：不要返回“缓存”对象。
        staffDao.detachAll();
        reusableAdapter.updateAll(staffDao.queryBuilder().orderAsc(StaffDao.Properties.StaffId).list());
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.newEmployee:
                startActivity(new Intent(this, NewEmployeeActivity.class));
                break;
            default:
                break;
        }
    }
}
