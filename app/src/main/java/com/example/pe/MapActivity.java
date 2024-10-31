package com.example.pe;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap gMap;
    String address;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        if (savedInstanceState == null) {
            SupportMapFragment mapFragment = new SupportMapFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.map, mapFragment)
                    .commit();
            mapFragment.getMapAsync(this);
        }
        Intent intent = getIntent();
        address = intent.getStringExtra("Address");

        Button buttonBack = findViewById(R.id.Back);
        buttonBack.setOnClickListener(view -> finish());
    }

    private void searchLocation(String locationName) {
        Geocoder geocoder = new Geocoder(MapActivity.this);
        List<Address> addressList = null;
        try {
            addressList = geocoder.getFromLocationName(locationName, 1);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(MapActivity.this, "Error retrieving location", Toast.LENGTH_SHORT).show();
        }

        if (addressList != null && !addressList.isEmpty()) {
            Address selectedAddress = addressList.get(0);
            LatLng selectedLocation = new LatLng(selectedAddress.getLatitude(), selectedAddress.getLongitude());
            putRedMarkerAndMoveCamera(selectedLocation, selectedAddress.getAddressLine(0));
        } else {
            Toast.makeText(MapActivity.this, "Location not found", Toast.LENGTH_SHORT).show();
        }
    }

    private void putRedMarkerAndMoveCamera(LatLng latLng, String title) {
        if (gMap  != null) {
            gMap.clear(); // Clear previous markers
            gMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(title)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15), 2000, null);
        } else {
            Log.e("MapsActivity", "myMap is null when trying to put marker");
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gMap = googleMap;
        String address = getIntent().getStringExtra("address");

        // Call findLocation here, after gMap is initialized
        if (address != null) {
            searchLocation(address);
        }
    }
}
