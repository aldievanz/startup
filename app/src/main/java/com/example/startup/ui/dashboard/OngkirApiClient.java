package com.example.startup.ui.dashboard;


import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OngkirApiClient {
    private static Retrofit retrofit = null;
    private static final String BASE_URL = "https://api.rajaongkir.com/starter/";

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
