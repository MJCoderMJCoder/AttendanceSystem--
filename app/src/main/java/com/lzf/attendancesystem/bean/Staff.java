package com.lzf.attendancesystem.bean;

/**
 * 员工实体类
 * Created by MJCoder on 2019-03-13.
 */
public class Staff {
    private int staffID;
    private String staffName;
    private String staffGender;
    private String staffDepartment;

    public Staff() {
    }

    public Staff(int staffID, String staffName, String staffGender, String staffDepartment) {
        this.staffID = staffID;
        this.staffName = staffName;
        this.staffGender = staffGender;
        this.staffDepartment = staffDepartment;
    }

    public int getStaffID() {
        return staffID;
    }

    public void setStaffID(int staffID) {
        this.staffID = staffID;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public String getStaffGender() {
        return staffGender;
    }

    public void setStaffGender(String staffGender) {
        this.staffGender = staffGender;
    }

    public String getStaffDepartment() {
        return staffDepartment;
    }

    public void setStaffDepartment(String staffDepartment) {
        this.staffDepartment = staffDepartment;
    }

    @Override
    public String toString() {
        return "Staff{" +
                "staffID=" + staffID +
                ", staffName='" + staffName + '\'' +
                ", staffGender='" + staffGender + '\'' +
                ", staffDepartment='" + staffDepartment + '\'' +
                '}';
    }
}
