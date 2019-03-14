package com.lzf.attendancesystem.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.lzf.attendancesystem.R;

public class AttendanceActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);
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
