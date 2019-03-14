package com.lzf.attendancesystem.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.lzf.attendancesystem.R;

public class AttendanceDataExportActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_data_export);
    }


    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.UExport:
                break;
            case R.id.nativeView:
                startActivity(new Intent(this, NativeViewActivity.class));
                break;
            default:
                break;
        }
    }
}
