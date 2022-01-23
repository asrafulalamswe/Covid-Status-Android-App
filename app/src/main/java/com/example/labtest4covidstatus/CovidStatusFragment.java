package com.example.labtest4covidstatus;

import android.annotation.SuppressLint;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.labtest4covidstatus.databinding.FragmentCovidStatusBinding;
import com.example.labtest4covidstatus.models.CovidResponseModel;
import com.example.labtest4covidstatus.permissions.LocationPermission;
import com.example.labtest4covidstatus.viewmodels.CovidViewModel;
import com.google.android.gms.common.api.Response;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class CovidStatusFragment extends Fragment {
    private double latitude;
    private double longitude;
    private FragmentCovidStatusBinding binding;
    private CovidViewModel viewModel;
    private FusedLocationProviderClient providerClient;
    private ActivityResultLauncher<String> launcher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    detectUserLocation();
                } else {
                    //show a dialog and explain user why you need this permission
                }
            });
    @SuppressLint("MissingPermission")
    private void detectUserLocation() {
        providerClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location == null) return;

                    viewModel.loadData();
                     latitude = location.getLatitude();
                     longitude = location.getLongitude();
                    Log.e("WeatherApp", "lat: "+latitude+",lon: "+longitude);
                });
    }

    public CovidStatusFragment() {
        // Required empty public constructor
    }
//
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.weather_menu, menu);
        final SearchView searchView = (SearchView) menu.findItem(R.id.item_search).getActionView();
        searchView.setQueryHint("Search a city");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query!=null){
                    viewModel.setCity(query);
                    viewModel.loadData();
                    searchView.setQuery(null,false);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.item_myLocation) {
            viewModel.setCity(getCity());
            viewModel.loadData();
        }
        return super.onOptionsItemSelected(item);
    }

    private String getCity(){
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());


       String cityName = null;
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);;
            if (addresses!=null && addresses.size()>0){
                cityName = addresses.get(0).getCountryName();
//                Toast.makeText(getActivity(), ""+cityName, Toast.LENGTH_SHORT).show();

            }
        } catch (IOException e) {
            Log.e("testtest", "getCity: "+e.getLocalizedMessage());
            e.printStackTrace();
        }
        return cityName;
    }
//
//
//
//

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCovidStatusBinding.inflate(inflater,container,false);
        viewModel = new ViewModelProvider(requireActivity()).get(CovidViewModel.class);
        viewModel.loadData();
//
        providerClient = LocationServices.getFusedLocationProviderClient(getActivity());
        if (LocationPermission.isLocatonPermissionGranted(getActivity())) {
            detectUserLocation();
        }else {
            LocationPermission.requestLocationPermission(launcher);
        }
        viewModel.setCity(getCity());
        viewModel.getResponseInfoLiveData().observe(getViewLifecycleOwner(), covidResponseModel -> {
            binding.countryTV.setText(covidResponseModel.getCountry());
            Picasso.get().load(covidResponseModel.getCountryInfo().getFlag())
                    .fit()
                    .into(binding.flagIV);
            binding.updateTimeTV.setText(new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z").format(new Date(covidResponseModel.getUpdated())));
            binding.caseTodayTV.setText(String.valueOf(covidResponseModel.getTodayCases()));
            binding.deathTodayTV.setText(String.valueOf(covidResponseModel.getTodayDeaths()));
            binding.recoverTodayTV.setText(String.valueOf(covidResponseModel.getTodayRecovered()));
            binding.totalcaseTV.setText(String.valueOf(covidResponseModel.getCases()));
            binding.totaldeathTV.setText(String.valueOf(covidResponseModel.getDeaths()));
            binding.totalrecoverTodayTV.setText(String.valueOf(covidResponseModel.getRecovered()));
        });


        return binding.getRoot();
    }
}