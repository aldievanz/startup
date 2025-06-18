package com.example.startup.foto;

import com.example.startup.ResponseUpload;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface RegisterAPI {
    @Multipart
    @POST("uploadimage.php") // ganti dengan endpoint milikmu
    Call<ResponseUpload> uploadImage(
            @Part MultipartBody.Part imageupload,
            @Part("id") RequestBody id
    );

}
