package com.example.labtest4covidstatus.viewmodels;

import android.location.Location;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.labtest4covidstatus.models.CovidResponseModel;
import com.example.labtest4covidstatus.network.CovidService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CovidViewModel extends ViewModel {
    private Location location;
    private MutableLiveData<CovidResponseModel> responseInfoLiveData = new MutableLiveData<>();

    private MutableLiveData<String> errorMessageLiveData = new MutableLiveData<>();
    private String city = "bangladesh";
    public void setCity(String city){
        this.city = city;
    }

    public void loadData(){
        fetchResponseData();
    }


    private void fetchResponseData(){
        final String endUrl = String.format("%s?yesterday=true&strict=true&query", city);
        CovidService.getService().getCurrentData(endUrl).enqueue(new Callback<CovidResponseModel>() {
            @Override
            public void onResponse(Call<CovidResponseModel> call, Response<CovidResponseModel> response) {
                if (response.code()==200){
                    Log.e("weather_test", ""+response.code() );
                    responseInfoLiveData.postValue(response.body());
                } else if (response.code() == 404){
                    errorMessageLiveData.postValue(response.message());
                    Log.e("weather_test", ""+response.code() );
                }
            }

            @Override
            public void onFailure(Call<CovidResponseModel> call, Throwable t) {
                Log.e("weather_test", ""+t );
            }
        });

    }



    public MutableLiveData<String> getErrorMessageLiveData() {
        return errorMessageLiveData;
    }

    public MutableLiveData<CovidResponseModel> getResponseInfoLiveData() {
        return responseInfoLiveData;
    }



}
