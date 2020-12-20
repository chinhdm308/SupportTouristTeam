package dmc.supporttouristteam.view.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
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
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import dmc.supporttouristteam.data.api.ApiUtils;
import dmc.supporttouristteam.data.model.Directions;
import dmc.supporttouristteam.data.model.LovePlace;
import dmc.supporttouristteam.data.model.MyCurrentAddress;
import dmc.supporttouristteam.data.model.MyLocation;
import dmc.supporttouristteam.data.model.Place;
import dmc.supporttouristteam.presenter.find_place.FindNearbyPlacesContract;
import dmc.supporttouristteam.presenter.find_place.FindNearbyPlacesPresenter;
import dmc.supporttouristteam.R;
import dmc.supporttouristteam.util.Common;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FindNearbyPlacesActivity extends FragmentActivity implements OnMapReadyCallback, ValueEventListener, FindNearbyPlacesContract.View, View.OnClickListener {

    private GoogleMap mMap;
    private Spinner spType;
    private Button buttonFind, buttonSavePlace, buttonDirect;
    private TextView textAddress, textName;

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
        placeTypeList = new String[]{"tourist_attraction", "park", "atm", "bank", "hospital", "restaurant"};
        // Initialize array of place name
        placeNameList = new String[]{"Địa điểm thu hút khách du lịch", "Công viên", "ATM", "Ngân hàng", "Bệnh viện", "Nhà hàng"};
        // Set adapter on spinner
        spType.setAdapter(new ArrayAdapter<>(FindNearbyPlacesActivity.this,
                android.R.layout.simple_spinner_dropdown_item, placeNameList));

        this.presenter = new FindNearbyPlacesPresenter(this, getApplicationContext());

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        Common.publicLocationRef.child(currentUser.getUid()).addValueEventListener(this);

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

    public void showDialogSavePlace(Place place) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(this)
                .inflate(R.layout.dialog_save_place, viewGroup, false);

        TextInputEditText edtPlaceName, edtAddress, edtDescription;

        edtPlaceName = dialogView.findViewById(R.id.edt_place_name);
        edtAddress = dialogView.findViewById(R.id.edt_address);
        edtDescription = dialogView.findViewById(R.id.edt_description);

        if (place != null) {
            edtPlaceName.setText(place.getName());
            edtAddress.setText(place.getVicinity());
        }

        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();
        alertDialog.setButton(Dialog.BUTTON_NEGATIVE, "Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.setButton(Dialog.BUTTON_POSITIVE, "Lưu", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!edtPlaceName.getText().toString().isEmpty() && !edtAddress.getText().toString().isEmpty()) {
                    LovePlace lovePlace = new LovePlace(edtPlaceName.getText().toString(), edtAddress.getText().toString(), edtDescription.getText().toString(), "default", place.getLat(), place.getLng());

                    Common.lovePlacesRef.child(currentUser.getUid())
                            .push()
                            .setValue(lovePlace, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                    if (error == null) {
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
                showDialogSavePlace(targetPlace);
                break;
            case R.id.button_direct:
                direct();
                break;
        }
    }

    private void direct() {
        ApiUtils.start(ApiUtils.BASE_URL_GOOGLE).apiCall()
                .loadDirect(myLocation.getLatitude() + "," + myLocation.getLongitude(),
                        targetPlace.getLat() + "," + targetPlace.getLng(),
                        getResources().getString(R.string.google_maps_key))
                .enqueue(new Callback<Directions>() {
                    @Override
                    public void onResponse(Call<Directions> call, Response<Directions> response) {
                        Log.d(Common.TAG, response.raw().request().url().toString());

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
        Glide.with(getApplicationContext()).asBitmap()
                .load(currentUser.getPhotoUrl())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
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
        Common.publicLocationRef.child(currentUser.getUid()).removeEventListener(this);
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Common.publicLocationRef.child(currentUser.getUid()).addValueEventListener(this);
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
                        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                        List<Address> list = null;
                        try {
                            list = geocoder.getFromLocation(
                                    myLocation.getLatitude(), myLocation.getLongitude(), 1);
                            if (list != null && list.size() > 0) {
                                Address address = list.get(0);
                                // sending back first address line and locality
                                textAddress.setText(address.getAddressLine(0));
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
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
            showMarkerUser();
        }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {

    }

    public void findCurrentPlace() {

        ApiUtils.start(ApiUtils.BASE_URL_GOOGLE).apiCall()
                .loadCurrentAddress(myLocation.getLatitude() + "," + myLocation.getLongitude(), getResources().getString(R.string.google_maps_key))
                .enqueue(new Callback<MyCurrentAddress>() {
                    @Override
                    public void onResponse(Call<MyCurrentAddress> call, Response<MyCurrentAddress> response) {
                        Log.d(Common.TAG, response.raw().request().url().toString());

                        textAddress.setText("Gần " + response.body().getResults()[0].getFormatted_address());

                        targetPlace = new Place();
                        targetPlace.setVicinity(textAddress.getText().toString());
                        targetPlace.setLat(myLocation.getLatitude());
                        targetPlace.setLng(myLocation.getLongitude());
                    }

                    @Override
                    public void onFailure(Call<MyCurrentAddress> call, Throwable t) {

                    }
                });

    }
}