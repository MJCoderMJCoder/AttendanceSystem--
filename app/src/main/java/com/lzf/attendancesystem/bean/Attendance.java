package com.lzf.attendancesystem.bean;

/**
 * Created by MJCoder on 2019-03-13.
 */

public class Attendance {
    private int staffID;
    private String staffName;
    private String signInTime;
    private String signOutTime;

    public Attendance() {
    }

    public Attendance(int staffID, String staffName, String signInTime, String signOutTime) {
        this.staffID = staffID;
        this.staffName = staffName;
        this.signInTime = signInTime;
        this.signOutTime = signOutTime;
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

    public String getSignInTime() {
        return signInTime;
    }

    public void setSignInTime(String signInTime) {
        this.signInTime = signInTime;
    }

    public String getSignOutTime() {
        return signOutTime;
    }

    public void setSignOutTime(String signOutTime) {
        this.signOutTime = signOutTime;
    }

    @Override
    public String toString() {
        return "Attendance{" +
                "staffID=" + staffID +
                ", staffName='" + staffName + '\'' +
                ", signInTime='" + signInTime + '\'' +
                ", signOutTime='" + signOutTime + '\'' +
                '}';
    }
}
