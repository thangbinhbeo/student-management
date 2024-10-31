package com.example.pe.api;

import com.example.pe.models.Student;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface StudentService {
    String STUDENTS = "Student";

    @GET(STUDENTS)
    Call<Student[]> getAllStudents();

    @GET(STUDENTS + "/{id}")
    Call<Student> getStudent(@Path("id") Object id);

    @POST(STUDENTS)
    Call<Student> createStudent(@Body Student student);

    @PUT(STUDENTS + "/{id}")
    Call<Student> updateStudent(@Path("id") Object id, @Body Student student);

    @DELETE(STUDENTS + "/{id}")
    Call<Void> deleteStudent(@Path("id") Object id);
}
