package dmc.supporttouristteam.Views.Activitis;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import dmc.supporttouristteam.Api.ApiUtils;
import dmc.supporttouristteam.Models.Directions;
import dmc.supporttouristteam.Models.LovePlace;
import dmc.supporttouristteam.Models.MyCurrentAddress;
import dmc.supporttouristteam.Models.MyLocation;
import dmc.supporttouristteam.Models.Place;
import dmc.supporttouristteam.Presenters.FindPlace.FindNearbyPlacesContract;
import dmc.supporttouristteam.Presenters.FindPlace.FindNearbyPlacesPresenter;
import dmc.supporttouristteam.R;
import dmc.supporttouristteam.Utils.Common;
import dmc.supporttouristteam.Utils.Config;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FindNearbyPlacesActivity extends FragmentActivity implements OnMapReadyCallback, ValueEventListener, FindNearbyPlacesContract.View, View.OnClickListener {

    private GoogleMap mMap;
    private Spinner spType;
    private Button buttonFind, buttonSavePlace, buttonDirect;
    private TextView textAddress, textName;

    private DatabaseReference publicLocationRef;
    private FirebaseUser currentUser;

    private MyLocation myLocation;

    private String[] placeTypeList, placeNameList;
    private FindNearbyPlacesPresenter presenter;
    private Marker myMarker;
    private LinearLayout layoutBottomSheet;
    private BottomSheetBehavior sheetBehavior;
    private boolean roomCheck;
    private Place targetPlace;

    private Polyline mLine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_nearby_places);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        // Initialize
        init();
    }

    private void init() {
        spType = findViewById(R.id.sp_type);
        buttonFind = findViewById(R.id.button_find);
        buttonFind.setOnClickListener(this);
        // Initialize array of place type
        placeTypeList = new String[]{"tourist_attraction", "park", "atm", "bank", "hospital", "movie_theater", "restaurant", "tourist"};
        // Initialize array of place name
        placeNameList = new String[]{"Tourist Attraction", "Pack", "ATM", "Bank", "Hospital", "Movie Theater", "Restaurant", "Tourist"};
        // Set adapter on spinner
        spType.setAdapter(new ArrayAdapter<>(FindNearbyPlacesActivity.this,
                android.R.layout.simple_spinner_dropdown_item, placeNameList));

        this.presenter = new FindNearbyPlacesPresenter(this, getApplicationContext());

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        publicLocationRef = FirebaseDatabase.getInstance().getReference(Config.RF_PUBLIC_LOCATION)
                .child(currentUser.getUid());
        publicLocationRef.addValueEventListener(this);

        layoutBottomSheet = findViewById(R.id.bottom_sheet);
        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
        sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        textAddress = findViewById(R.id.text_address);
        textName = findViewById(R.id.text_name);

        buttonSavePlace = findViewById(R.id.button_save_place);
        buttonSavePlace.setOnClickListener(this);

        roomCheck = false;

        buttonDirect = findViewById(R.id.button_direct);
        buttonDirect.setOnClickListener(this);
    }

    public void showDialog(Place place) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(this)
                .inflate(R.layout.dialog_save_place, viewGroup, false);

        TextInputEditText etPlaceName, etAddress, etDescription;
        Button buttonCancel, buttonSave;

        etPlaceName = dialogView.findViewById(R.id.et_place_name);
        etAddress = dialogView.findViewById(R.id.et_address);
        etDescription = dialogView.findViewById(R.id.et_description);
        buttonCancel = dialogView.findViewById(R.id.button_cancel);
        buttonSave = dialogView.findViewById(R.id.button_save);

        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!etPlaceName.getText().toString().isEmpty() && !etAddress.getText().toString().isEmpty()) {
                    DatabaseReference lovePlacesRef = FirebaseDatabase.getInstance().getReference(Config.RF_LOVE_PLACES).child(currentUser.getUid());

                    LovePlace lovePlace = new LovePlace(etPlaceName.getText().toString(), etAddress.getText().toString(), etDescription.getText().toString(), "default", place.getLat(), place.getLng());

                    lovePlacesRef.push().setValue(lovePlace, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                            if (error != null) {
                                Toast.makeText(getApplicationContext(), "Lưu thành công", Toast.LENGTH_SHORT).show();
                                alertDialog.dismiss();
                            } else {
                                Toast.makeText(getApplicationContext(), "Lỗi", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "Bạn nhập thiếu thông tin", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (place != null) {
            etPlaceName.setText(place.getName());
            etAddress.setText(place.getVicinity());
        }

        alertDialog.show();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_find:
                mMap.clear();
                showMarkerUser();
                presenter.doSearchPlacesNearYou(myLocation, placeTypeList, spType.getSelectedItemPosition());
                break;
            case R.id.button_save_place:
                showDialog(targetPlace);
                break;
            case R.id.button_direct:
                direct();
                break;
        }
    }

    private void direct() {
        ApiUtils.start().apiCall().loadDirect(myLocation.getLatitude() + "," + myLocation.getLongitude(), targetPlace.getLat() + "," + targetPlace.getLng(), getResources().getString(R.string.google_maps_key))
                .enqueue(new Callback<Directions>() {
                    @Override
                    public void onResponse(Call<Directions> call, Response<Directions> response) {
                        Log.d(Config.TAG, response.raw().request().url().toString());

                        List<LatLng> latLngs = new ArrayList<>();
                        Directions.Route[] routes = response.body().getRoutes();
                        Directions.Leg[] legs = routes[0].getLegs();
                        Directions.Leg.Step[] steps = legs[0].getSteps();

                        for (Directions.Leg.Step step : steps) {
                            LatLng latLngStart = new LatLng(step.getStart_location().getLat(),
                                    step.getStart_location().getLng());

                            LatLng latLngEnd = new LatLng(step.getEnd_location().getLat(),
                                    step.getEnd_location().getLng());

                            latLngs.add(latLngStart);
                            latLngs.add(latLngEnd);
                        }

                        PolylineOptions polylineOptions = new PolylineOptions();
                        polylineOptions.addAll(latLngs);

                        if (mLine != null) mLine.remove();
                        mLine = mMap.addPolyline(polylineOptions);
                        mLine.setColor(Color.BLUE);
                        mLine.setWidth(6);
                    }

                    @Override
                    public void onFailure(Call<Directions> call, Throwable t) {

                    }
                });
    }

    @Override
    public void showPlace(Place place) {
        LatLng userMarker = new LatLng(place.getLat(), place.getLng());
        Glide.with(getApplicationContext()).asBitmap().load(place.getIcon()).diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        Bitmap image = Common.createUserBitmap(getApplicationContext(), resource);
                        mMap.addMarker(new MarkerOptions()
                                .position(userMarker)
                                .title(place.getName())
                                .icon(BitmapDescriptorFactory.fromBitmap(image))
                                .snippet(place.getVicinity()))
                                .setTag(place);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }

    @Override
    public void showMarkerUser() {
        LatLng userMarker = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
        if (!roomCheck) {
            roomCheck = !roomCheck;
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.builder()
                    .target(userMarker).zoom(16f).build()));
        }
        Glide.with(getApplicationContext()).asBitmap().load(currentUser.getPhotoUrl()).diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        Bitmap icon = Common.createUserBitmap(getApplicationContext(), resource);
                        if (myMarker != null) myMarker.remove();
                        myMarker = mMap.addMarker(new MarkerOptions()
                                .position(userMarker)
                                .title(currentUser.getDisplayName())
                                .icon(BitmapDescriptorFactory.fromBitmap(icon))
                                .snippet(Common.convertTimeStampToString(myLocation.getTime())));
                        myMarker.setTag(userMarker);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
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

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (marker.getTag() != null) {
                    if (marker.getTag().getClass() == Place.class) {
                        buttonDirect.setVisibility(View.VISIBLE);
                        Place place = (Place) marker.getTag();
                        textName.setText(place.getName() + "\nTọa độ: " + place.getLat() + " " + place.getLng());
                        textAddress.setText(place.getVicinity());
                        targetPlace = place;
                    } else {
                        buttonDirect.setVisibility(View.GONE);
                        LatLng latLng = (LatLng) marker.getTag();
                        textName.setText(currentUser.getDisplayName() + "\nTọa độ: " + latLng.latitude + " " + latLng.longitude);
//                        textAddress.setText("< trống >");
                        findCurrentPlace();
                        targetPlace = null;
                    }
                    // change the state of the bottom sheet
                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

                }
                return false;
            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                // change the state of the bottom sheet
                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {
        if (snapshot.getValue() != null) {
            myLocation = snapshot.getValue(MyLocation.class);
            // Add Marker
            showMarkerUser();
        }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {

    }

    public void findCurrentPlace() {

        ApiUtils.start().apiCall()
                .loadCurrentAddress(myLocation.getLatitude() + "," + myLocation.getLongitude(), getResources().getString(R.string.google_maps_key))
                .enqueue(new Callback<MyCurrentAddress>() {
                    @Override
                    public void onResponse(Call<MyCurrentAddress> call, Response<MyCurrentAddress> response) {
                        Log.d(Config.TAG, response.raw().request().url().toString());

                        textAddress.setText("Gần " + response.body().getResults()[0].getFormatted_address());

                    }

                    @Override
                    public void onFailure(Call<MyCurrentAddress> call, Throwable t) {

                    }
                });

    }
}