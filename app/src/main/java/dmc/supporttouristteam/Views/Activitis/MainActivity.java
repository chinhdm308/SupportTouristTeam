package dmc.supporttouristteam.Views.Activitis;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import dmc.supporttouristteam.R;
import dmc.supporttouristteam.Service.MyLocationReceiver;
import dmc.supporttouristteam.Views.Fragments.ChatsFragment;
import dmc.supporttouristteam.Views.Fragments.PeopleFragment;

public class MainActivity extends AppCompatActivity implements ChipNavigationBar.OnItemSelectedListener {

    private ChipNavigationBar navigationBar;

    private LocationRequest locationRequest;
    private FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        init();
        navigationBar.setOnItemSelectedListener(this);
        if (savedInstanceState == null) {
            navigationBar.setItemSelected(R.id.menu_chats, true);
        }

        // Update location
        updateLocation();
    }

    private void updateLocation() {
        buildLocationRequest();
    }

    private void buildLocationRequest() {
        locationRequest = new LocationRequest();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, getPendingIntent());
    }

    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(MainActivity.this, MyLocationReceiver.class);
        intent.setAction(MyLocationReceiver.ACTION);
        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void init() {
        navigationBar = findViewById(R.id.navigation_bar);
    }

    @Override
    public void onItemSelected(int i) {
        if (i == R.id.menu_chats) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ChatsFragment()).commit();
        }
        if (i == R.id.menu_people) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PeopleFragment()).commit();
        }
    }
}