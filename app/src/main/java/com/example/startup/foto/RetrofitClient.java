package com.example.startup.foto;



import retrofit2. Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static Retrofit retro =null;

    private static Retrofit getClient(){
        if(retro==null) {
            retro = new Retrofit.Builder()
                    .baseUrl(new ServerAPI().BASE_URL_IMAGE)
                    .addConverterFactory (GsonConverterFactory.create())
                    .build();

        }
        return retro;
    }

    public static com.example.startup.foto.RegisterAPI getApiServices() { return getClient().create(RegisterAPI.class); }
}