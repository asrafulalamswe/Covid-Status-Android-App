package com.example.labtest4covidstatus.network;

import com.example.labtest4covidstatus.models.CovidResponseModel;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface CovidServiceApi {
    @GET
    Call<CovidResponseModel> getCurrentData(@Url String endUrl);

}
