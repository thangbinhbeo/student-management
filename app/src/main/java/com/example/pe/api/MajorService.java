package com.example.pe.api;

import com.example.pe.models.Major;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface MajorService {
    String MAJOR = "Major";

    @GET(MAJOR)
    Call<Major[]> getAllMajors();

    @GET(MAJOR + "/{id}")
    Call<Major> getMajor(@Path("id") Object id);

    @POST(MAJOR)
    Call<Major> createMajor(@Body Major major);

    @PUT(MAJOR + "/{id}")
    Call<Major> updateMajor(@Path("id") Object id, @Body Major major);

    @DELETE(MAJOR + "/{id}")
    Call<Void> deleteMajor(@Path("id") Object id);
}
