package com.example.pe.models;

import java.util.Date;

public class Student {
    private int ID;
    private String name;
    private long date;
    private String gender;
    private String email;
    private String address;
    private int majorID;

    public Student(int ID, String name, long date, String gender, String email, String address, int majorID) {
        this.ID = ID;
        this.name = name;
        this.date = date;
        this.gender = gender;
        this.email = email;
        this.address = address;
        this.majorID = majorID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getMajorID() {
        return majorID;
    }

    public void setMajorID(int majorID) {
        this.majorID = majorID;
    }
}
