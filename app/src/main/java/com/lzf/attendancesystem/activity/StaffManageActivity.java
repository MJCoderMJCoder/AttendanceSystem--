package com.lzf.attendancesystem.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;

import com.lzf.attendancesystem.R;
import com.lzf.attendancesystem.bean.Staff;
import com.lzf.attendancesystem.util.ReusableAdapter;

import java.util.ArrayList;
import java.util.List;

public class StaffManageActivity extends AppCompatActivity {

    private ReusableAdapter<Staff> reusableAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_manage);
        ListView employeeList = findViewById(R.id.employeeList);
        List<Staff> staffs = new ArrayList<Staff>();
        staffs.add(new Staff(1, "阿郎", "女", "软件部"));
        staffs.add(new Staff(2, "法师", "女", "软件部"));
        staffs.add(new Staff(3, "法蒂", "男", "软件部"));
        staffs.add(new Staff(4, "陆士仁", "男", "软件部"));
        staffs.add(new Staff(5, "纸纷飞", "男", "软件部"));
        staffs.add(new Staff(6, "㻰林俊", "女", "软件部"));
        staffs.add(new Staff(7, "依然浅笑", "男", "软件部"));
        staffs.add(new Staff(8, "达萨罗", "男", "软件部"));
        staffs.add(new Staff(9, "范德萨", "男", "软件部"));
        staffs.add(new Staff(10, "栾克军", "女", "软件部"));
        staffs.add(new Staff(11, "欧赔", "男", "软件部"));
        staffs.add(new Staff(12, "刘飞大", "男", "软件部"));
        reusableAdapter = new ReusableAdapter<Staff>(staffs, R.layout.item_staff_manage) {
            @Override
            public void bindView(ViewHolder holder, Staff obj) {
                holder.setText(R.id.employeeID, obj.getStaffID() + "");
                holder.setText(R.id.name, obj.getStaffName());
                holder.setText(R.id.gender, obj.getStaffGender());
                holder.setText(R.id.department, obj.getStaffDepartment());
            }
        };
        employeeList.setAdapter(reusableAdapter);
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
