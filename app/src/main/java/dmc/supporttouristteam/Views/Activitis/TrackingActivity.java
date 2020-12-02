package dmc.supporttouristteam.Views.Activitis;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import dmc.supporttouristteam.Models.GroupInfo;
import dmc.supporttouristteam.Models.MyLocation;
import dmc.supporttouristteam.Models.User;
import dmc.supporttouristteam.R;
import dmc.supporttouristteam.Utils.Common;
import dmc.supporttouristteam.Utils.Config;

public class TrackingActivity extends FragmentActivity implements OnMapReadyCallback, ValueEventListener {

    private GoogleMap mMap;
    private DatabaseReference publicLocationRef, usersRef;
    private GroupInfo groupInfo;
    private boolean roomCheck = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        groupInfo = (GroupInfo) getIntent().getSerializableExtra(Config.EXTRA_GROUP_INFO);

        usersRef = FirebaseDatabase.getInstance().getReference(Config.RF_USERS);

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
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {
        mMap.clear();
        for (DataSnapshot data : snapshot.getChildren()) {
            if (data.exists()) {
                String uid = data.getKey();
                if (groupInfo.getChatList().contains(uid)) {
                    MyLocation location = data.getValue(MyLocation.class);
                    LatLng curUser = new LatLng(location.getLatitude(), location.getLongitude());
                    if (uid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        // Add Marker
                        Glide.with(getApplicationContext()).asBitmap()
                                .load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl())
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(new CustomTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                        Bitmap icon = createUserBitmap(resource);
                                        mMap.addMarker(new MarkerOptions()
                                                .position(curUser)
                                                .title(FirebaseAuth.getInstance().getCurrentUser().getDisplayName())
                                                .icon(BitmapDescriptorFactory.fromBitmap(icon))
                                                .snippet(Common.convertTimeStampToString(location.getTime())));
                                        if (!roomCheck) {
                                            roomCheck = !roomCheck;
                                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.builder()
                                                    .target(curUser).zoom(16f).build()));
                                        }
                                    }

                                    @Override
                                    public void onLoadCleared(@Nullable Drawable placeholder) {

                                    }
                                });
                    } else {
                        // Add Marker
                        usersRef.child(uid).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    User user = snapshot.getValue(User.class);
                                    Glide.with(getApplicationContext()).asBitmap()
                                            .load(user.getProfileImg())
                                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                                            .into(new CustomTarget<Bitmap>() {
                                                @Override
                                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                                    Bitmap icon = createUserBitmap(resource);
                                                    mMap.addMarker(new MarkerOptions()
                                                            .position(curUser)
                                                            .title(user.getDisplayName())
                                                            .icon(BitmapDescriptorFactory.fromBitmap(icon))
                                                            .snippet(Common.convertTimeStampToString(location.getTime())));
                                                }

                                                @Override
                                                public void onLoadCleared(@Nullable Drawable placeholder) {

                                                }
                                            });
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



    // utility
    private int dp(float value) {
        if (value == 0) {
            return 0;
        }
        return (int) Math.ceil(getResources().getDisplayMetrics().density * value);
    }

    private Bitmap createUserBitmap(Bitmap bm) {
        Bitmap result = null;
        try {
            result = Bitmap.createBitmap(dp(62), dp(76), Bitmap.Config.ARGB_8888);
            result.eraseColor(Color.TRANSPARENT);
            Canvas canvas = new Canvas(result);
            Drawable drawable = getDrawable(R.drawable.livepin);
            drawable.setBounds(0, 0, dp(62), dp(76));
            drawable.draw(canvas);

            Paint roundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            RectF bitmapRect = new RectF();
            canvas.save();

            Bitmap bitmap = bm;

            if (bitmap == null) {
                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.avatar);
            }

            if (bitmap != null) {
                BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                Matrix matrix = new Matrix();
                float scale = dp(52) / (float) bitmap.getWidth();
                matrix.postTranslate(dp(5), dp(5));
                matrix.postScale(scale, scale);
                roundPaint.setShader(shader);
                shader.setLocalMatrix(matrix);
                bitmapRect.set(dp(5), dp(5), dp(52 + 5), dp(52 + 5));
                canvas.drawRoundRect(bitmapRect, dp(26), dp(26), roundPaint);
            }
            canvas.restore();
            canvas.setBitmap(bitmap);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return result;
    }
}