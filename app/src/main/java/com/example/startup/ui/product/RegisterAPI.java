package com.example.startup.ui.product;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RegisterAPI {

    // Register
    @FormUrlEncoded
    @POST("post_register.php")
    Call<ResponseBody> register(
            @Field("email") String email,
            @Field("nama") String nama,
            @Field("password") String password
    );

    // Login
    @FormUrlEncoded
    @POST("post_login.php")
    Call<ResponseBody> login(
            @Field("email") String email,
            @Field("password") String password
    );

    // Get Profile
    @GET("get_profile.php")
    Call<ResponseBody> getProfile(
            @Query("email") String email
    );

    // Update Profile
    @FormUrlEncoded
    @POST("post_profile.php")
    Call<ResponseBody> updateProfile(
            @Field("nama") String nama,
            @Field("alamat") String alamat,
            @Field("kota") String kota,
            @Field("provinsi") String provinsi,
            @Field("telp") String telp,
            @Field("kodepos") String kodepos,
            @Field("email") String email
    );

    // Get all products
    @GET("get_product.php")
    Call<List<Product>> getProducts();

    // Get view count for a product
    @FormUrlEncoded
    @POST("get_view.php")
    Call<ViewResponse> getView(@Field("kode") String kode);

    // Update view count for a product
    @FormUrlEncoded
    @POST("updateView.php")
    Call<Integer> updateView(@Field("kode") String kode);

}
