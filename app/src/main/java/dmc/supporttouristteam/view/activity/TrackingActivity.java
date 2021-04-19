package dmc.supporttouristteam.view.activity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.SphericalUtil;

import java.util.HashMap;
import java.util.Map;

import dmc.supporttouristteam.R;
import dmc.supporttouristteam.data.api.ApiUtils;
import dmc.supporttouristteam.data.model.fb.PublicLocation;
import dmc.supporttouristteam.data.model.fb.GroupInfo;
import dmc.supporttouristteam.data.model.fb_mes.MyRequest;
import dmc.supporttouristteam.data.model.fb_mes.MyResponse;
import dmc.supporttouristteam.data.model.fb.User;
import dmc.supporttouristteam.util.Common;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrackingActivity extends FragmentActivity implements OnMapReadyCallback, ValueEventListener, View.OnClickListener {

    private GoogleMap mMap;
    private GroupInfo groupInfo;
    private boolean roomCheck = false;

    private FirebaseUser currentUser;
    private PublicLocation mLocation;

    private EditText edtMessage;
    private ImageView imageSend;
    private String TAG = "tagTrackingActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Common.publicLocationRef.addValueEventListener(this);
    }

    @Override
    protected void onStop() {
        Common.publicLocationRef.removeEventListener(this);
        super.onStop();
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
    @SuppressLint({"MissingPermission", "PotentialBehaviorOverride"})
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                LinearLayout info = new LinearLayout(getApplicationContext());
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(getApplicationContext());
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setGravity(Gravity.CENTER_HORIZONTAL);
                title.setText(marker.getTitle());

                TextView snippet = new TextView(getApplicationContext());
                snippet.setTextColor(Color.GRAY);
                snippet.setGravity(Gravity.CENTER_HORIZONTAL);
                snippet.setText(marker.getSnippet());

                Button buttonDirect = new Button(getApplicationContext());
                buttonDirect.setText("Chỉ đường");
                buttonDirect.setTextSize(10);
                buttonDirect.setBackgroundColor(Color.BLACK);
                buttonDirect.setTextColor(Color.WHITE);

                info.addView(title);
                info.addView(snippet);
                info.addView(buttonDirect);

                return info;
            }
        });
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {
        mMap.clear();
        for (DataSnapshot data : snapshot.getChildren()) {
            if (data.exists()) {
                String uid = data.getKey();
                if (groupInfo.getChatList().contains(uid)) {
                    PublicLocation location = data.getValue(PublicLocation.class);
                    LatLng curUser = new LatLng(location.getLatitude(), location.getLongitude());
                    if (uid.equals(currentUser.getUid())) {
                        mLocation = location;
                        if (!roomCheck) {
                            roomCheck = !roomCheck;
                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.builder()
                                    .target(curUser).zoom(16f).build()));
                        }
                        // Add Marker
//                        addMarker(currentUser.getDisplayName(), currentUser.getPhotoUrl().toString(), location, curUser);
                    } else {
                        Common.usersRef.child(uid).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    User user = snapshot.getValue(User.class);
                                    addMarker(user.getDisplayName(), user.getProfileImg(), location, curUser);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
            }
        }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_send:
                String message = edtMessage.getText().toString();
                if (!message.isEmpty()) {
                    sendWarningMessage(message);
                    edtMessage.setText("");
                }
                break;
        }
    }

    private void sendWarningMessage(String message) {
        Common.tokensRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                MyRequest myRequest = new MyRequest();
                Map<String, String> dataSend = new HashMap<>();
                dataSend.put(Common.CODE, Common.CODE_WARNING_MESSAGE);
                if (Common.groupClicked.getType() == 2) {
                    dataSend.put(Common.TITLE, "Thông báo từ " + currentUser.getDisplayName());
                    dataSend.put(Common.CONTENT, message);
                } else {
                    dataSend.put(Common.TITLE, "Thông báo từ nhóm " + Common.groupClicked.getName());
                    dataSend.put(Common.CONTENT, currentUser.getDisplayName() + ": " + message);
                }

                for (DataSnapshot i : snapshot.getChildren()) {
                    String key = i.getKey();
                    if (Common.groupClicked.getChatList().contains(key) && !key.equals(currentUser.getUid())) {
                        myRequest.setTo(i.getValue(String.class));
                        myRequest.setData(dataSend);

                        ApiUtils.start(ApiUtils.BASE_URL_FIREBASE).apiCall()
                                .sendWarningMessage(myRequest)
                                .enqueue(new Callback<MyResponse>() {
                                    @Override
                                    public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                        if (response.code() == 200) {
                                            if (response.body().success == 1) {
                                                Toast.makeText(getApplicationContext(),
                                                        "Gửi thành công",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<MyResponse> call, Throwable t) {

                                    }
                                });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void init() {
        groupInfo = (GroupInfo) getIntent().getSerializableExtra(Common.EXTRA_GROUP_INFO);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        Common.publicLocationRef.addValueEventListener(this);

        edtMessage = findViewById(R.id.edt_message);
        imageSend = findViewById(R.id.image_send);
        imageSend.setOnClickListener(this);
    }

    private void addMarker(String title, String url, PublicLocation location, LatLng curUser) {
        double distance = Math.round(SphericalUtil.computeDistanceBetween(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()), curUser));

        Glide.with(getApplicationContext()).asBitmap()
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        Bitmap icon = Common.createUserBitmap(TrackingActivity.this, resource);
                        mMap.addMarker(new MarkerOptions()
                                .position(curUser)
                                .title(title)
                                .icon(BitmapDescriptorFactory.fromBitmap(icon))
                                .snippet(Common.convertTimeStampToString(location.getTime()) + "\nKhoảng cách: " + distance + " mét"));
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }

}