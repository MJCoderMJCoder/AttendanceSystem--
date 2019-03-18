package com.lzf.attendancesystem.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.lzf.attendancesystem.R;
import com.lzf.attendancesystem.ZffApplication;
import com.lzf.attendancesystem.bean.Admin;
import com.lzf.attendancesystem.bean.AdminDao;

import java.util.List;

public class ModifyAccountActivity extends AppCompatActivity {
    private EditText accountEdit;
    private String account;
    private EditText oldPwdEdit;
    private String oldPwd;
    private EditText newPwdEdit;
    private String newPwd;
    private EditText affirmPwdEdit;
    private String affirmPwd;
    private EditText nameEdit;
    private String name;
    private EditText emailEdit;
    private String email;
    private EditText phoneEdit;
    private String phone;
    private String gender;

    private AdminDao adminDao = ZffApplication.getDaoSession(this).getAdminDao();
    private Admin admin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_account);

        List<Admin> admins = adminDao.queryBuilder().orderAsc(AdminDao.Properties.AdminId).list();
        if (admins != null && admins.size() > 0) {
            RadioGroup radioGroup = findViewById(R.id.radioGroup);
            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    gender = ((RadioButton) findViewById(checkedId)).getText().toString().trim();
                }
            });
            admin = admins.get(0);
            account = admin.getAdminAccount();
            oldPwd = admin.getAdminPassword();
            newPwd = admin.getAdminPassword();
            affirmPwd = admin.getAdminPassword();
            name = admin.getAdminName();
            email = admin.getAdminEmail();
            phone = admin.getAdminPhone();
            accountEdit = findViewById(R.id.accountEdit);
            accountEdit.setText(account);
            oldPwdEdit = findViewById(R.id.oldPwdEdit);
            oldPwdEdit.setText(oldPwd);
            newPwdEdit = findViewById(R.id.newPwdEdit);
            newPwdEdit.setText(newPwd);
            affirmPwdEdit = findViewById(R.id.affirmPwdEdit);
            affirmPwdEdit.setText(affirmPwd);
            nameEdit = findViewById(R.id.nameEdit);
            nameEdit.setText(name);
            emailEdit = findViewById(R.id.emailEdit);
            emailEdit.setText(email);
            phoneEdit = findViewById(R.id.phoneEdit);
            phoneEdit.setText(phone);
            gender = admin.getAdminGender();
            if ("男".equals(gender)) {
                findViewById(R.id.man).performClick();
            } else if ("女".equals(gender)) {
                findViewById(R.id.woman).performClick();
            }
        } else {
            Toast.makeText(this, "管理员信息丢失", Toast.LENGTH_SHORT).show();
            finish();
        }
    }


    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.modify:
                try {
                    account = accountEdit.getText().toString();
                    if ("".equals(account)) {
                        Toast.makeText(this, "请输入账号", Toast.LENGTH_SHORT).show();
                    } else {
                        oldPwd = oldPwdEdit.getText().toString();
                        if ("".equals(oldPwd)) {
                            Toast.makeText(this, "请输入原始密码进行验证", Toast.LENGTH_SHORT).show();
                        } else {
                            if (!oldPwd.equals(admin.getAdminPassword())) {
                                Toast.makeText(this, "原始密码不正确", Toast.LENGTH_SHORT).show();
                            } else {
                                newPwd = newPwdEdit.getText().toString();
                                affirmPwd = affirmPwdEdit.getText().toString();
                                if (!affirmPwd.equals(newPwd)) {
                                    Toast.makeText(this, "两次输入的新密码不一致！请重新输入", Toast.LENGTH_LONG).show();
                                } else {
                                    name = nameEdit.getText().toString();
                                    if ("".equals(name)) {
                                        Toast.makeText(this, "请输入姓名", Toast.LENGTH_SHORT).show();
                                    } else {
                                        email = emailEdit.getText().toString();
                                        if ("".equals(email)) {
                                            Toast.makeText(this, "请输入邮箱", Toast.LENGTH_SHORT).show();
                                        } else {
                                            phone = phoneEdit.getText().toString();
                                            if ("".equals(phone)) {
                                                Toast.makeText(this, "请输入手机号", Toast.LENGTH_SHORT).show();
                                            } else {
                                                admin.setAdminAccount(account);
                                                admin.setAdminPassword(newPwd);
                                                admin.setAdminName(name);
                                                admin.setAdminEmail(email);
                                                admin.setAdminPhone(phone);
                                                admin.setAdminGender(gender);
                                                adminDao.update(admin);
                                                finish();
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "修改信息失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }
}
