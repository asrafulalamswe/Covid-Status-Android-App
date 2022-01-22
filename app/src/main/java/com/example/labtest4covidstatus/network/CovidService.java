package com.example.labtest4covidstatus.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CovidService {
    public static CovidServiceApi getService(){
        final Retrofit retrofit = new Retrofit.Builder().baseUrl("https://corona.lmao.ninja/v2/countries/")
                .addConverterFactory(GsonConverterFactory.create()).build();
        return retrofit.create(CovidServiceApi.class);
    }
}
