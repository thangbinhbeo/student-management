package com.example.pe.models;

import androidx.annotation.NonNull;

public class Major {
    private int majorID;
    private String majorName;

    public Major(int majorID, String majorName) {
        this.majorID = majorID;
        this.majorName = majorName;
    }

    public int getMajorID() {
        return majorID;
    }

    public void setMajorID(int majorID) {
        this.majorID = majorID;
    }

    public String getMajorName() {
        return majorName;
    }

    public void setMajorName(String majorName) {
        this.majorName = majorName;
    }

    @NonNull
    @Override
    public String toString() {
        return majorName;
    }
}
