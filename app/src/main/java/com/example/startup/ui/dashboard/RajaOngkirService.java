package com.example.startup.ui.dashboard;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RajaOngkirService {

    @GET("province")
    Call<RajaOngkirProvinseResponse> getProvinces(@Header("key") String apiKey);

    @GET("city")
    Call<RajaOngkirCityResponse> getCitiesByProvince(
            @Query("province") String provinceId,
            @Header("key") String apiKey
    );

    @FormUrlEncoded
    @POST("cost")
    Call<RajaOngkirResponse> getOngkir(
            @Header("key") String apiKey,
            @Field("origin") String origin,
            @Field("destination") String destination,
            @Field("weight") int weight,
            @Field("courier") String courier
    );
}
