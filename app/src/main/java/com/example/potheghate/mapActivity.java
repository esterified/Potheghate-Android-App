package com.example.potheghate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.dingi.dingisdk.Dingi;
import com.dingi.dingisdk.constants.Style;
import com.dingi.dingisdk.maps.DingiMap;
import com.dingi.dingisdk.maps.MapView;

public class mapActivity extends AppCompatActivity {
    private MapView mapView;
    private DingiMap map;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Dingi.getInstance(this, "EjFUMTUMKFcnJ2VzRnL39Cd2ixtHScJ2p0C1vhP2");
        Dingi.getInstance(this, "a8ba6c9e-a3cd-493d-b4e5-f3aebe813ee5");
        setContentView(R.layout.activity_map);
        mapView = findViewById(R.id.dingi_map);
        mapView.onCreate(savedInstanceState);
      //  mapView.getMapAsync(dingiMap -> dingiMap.setStyleUrl(Style.DINGI_ENGLISH));

    }
    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}