package com.example.pe.api;

public class Repository {
    public static StudentService getStudentService() {
        return APIClient.getClient().create(StudentService.class);
    }

    public static MajorService getMajorService() {
        return APIClient.getClient().create(MajorService.class);
    }
}
