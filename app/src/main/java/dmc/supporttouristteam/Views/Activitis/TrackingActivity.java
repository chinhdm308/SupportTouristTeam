package dmc.supporttouristteam.Views.Activitis;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import dmc.supporttouristteam.Models.GroupInfo;
import dmc.supporttouristteam.Models.MyLocation;
import dmc.supporttouristteam.R;
import dmc.supporttouristteam.Utils.Common;
import dmc.supporttouristteam.Utils.Config;

public class TrackingActivity extends FragmentActivity implements OnMapReadyCallback, ValueEventListener {

    private GoogleMap mMap;
    private DatabaseReference publicLocationRef;
    private GroupInfo groupInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        groupInfo = (GroupInfo) getIntent().getSerializableExtra(Config.EXTRA_GROUP_INFO);

        registerEventRealtime(groupInfo);
    }

    private void registerEventRealtime(GroupInfo groupInfo) {
        publicLocationRef = FirebaseDatabase.getInstance().getReference(Config.RF_PUBLIC_LOCATION);
        publicLocationRef.addValueEventListener(this);
    }

    @Override
    protected void onStop() {
        publicLocationRef.removeEventListener(this);
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        publicLocationRef.addValueEventListener(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        mMap.getUiSettings().setZoomControlsEnabled(true);
        Toast.makeText(this, "Map ready", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {
        LatLng curUser = null;
        for (DataSnapshot data : snapshot.getChildren()) {
            if (data.getValue() != null) {
                MyLocation location = data.getValue(MyLocation.class);
                if (groupInfo.getChatList().contains(location.getId())) {
                    if (location.getId().equals(Common.loggedUser.getId()))
                        curUser = new LatLng(location.getLatitude(), location.getLongitude());
                    // Add Marker
                    LatLng userMarker = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(userMarker)
                            .title("Hehe")
                            .snippet(Common.getDateFormatted(Common.convertTimeStampToDate(location.getTime()))));
                }
            }
        }

        if (curUser != null) {
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.builder()
                    .target(curUser).zoom(16f).build()));
        }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {

    }
}