package dmc.supporttouristteam.Views.Activitis;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;

import dmc.supporttouristteam.Api.AppApi;
import dmc.supporttouristteam.Api.Place;
import dmc.supporttouristteam.Models.MyLocation;
import dmc.supporttouristteam.R;
import dmc.supporttouristteam.Utils.Common;
import dmc.supporttouristteam.Utils.Config;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FindNearbyPlacesActivity extends FragmentActivity implements OnMapReadyCallback, ValueEventListener {

    private GoogleMap mMap;
    private Spinner spType;
    private Button buttonFind;

    private double currentLat = 0, currentLong = 0;

    private DatabaseReference publicLocationRef;

    private AppApi executeApi;

    private String[] placeTypeList, placeNameList;
    private List<Place> placeList;

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

        initRetrofitApi();

        registerEventRealtime();

        buttonFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = spType.getSelectedItemPosition();
                executeByRetrofit(i);
            }
        });
    }

    private void init() {
        spType = findViewById(R.id.sp_type);
        buttonFind = findViewById(R.id.button_find);
        // Initialize place list
        placeList = new ArrayList<>();
        // Initialize array of place type
        placeTypeList = new String[]{"atm", "bank", "hospital", "movie_theater", "restaurant", "tourist"};
        // Initialize array of place name
        placeNameList = new String[]{"ATM", "Bank", "Hospital", "Movie Theater", "Restaurant", "Tourist"};
        // Set adapter on spinner
        spType.setAdapter(new ArrayAdapter<>(FindNearbyPlacesActivity.this,
                android.R.layout.simple_spinner_dropdown_item, placeNameList));
    }

    private void initRetrofitApi() {
        // Execute place task method to download json data
        // Tạo instance của Retrofit2 :
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/maps/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        // Thực hiện Request đến API và xử lý kết quả trả về :
        executeApi = retrofit.create(AppApi.class);
    }

    private void executeByRetrofit(int i) {
        placeList.clear();
        executeApi.loadNearPlaces(currentLat + "," + currentLong,
                5000,
                placeTypeList[i],
                true,
                "prominence",
                getResources().getString(R.string.google_maps_key))
                .enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        Log.d(Config.TAG, "Thanh cong nhe");
                        JsonArray results = response.body().getAsJsonArray("results");
                        mMap.clear();
                        for (JsonElement elem : results) {
                            JsonObject object = elem.getAsJsonObject();
                            Place place = parseJson(object);
                            placeList.add(place);
                            addMarker(place);
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        Log.d(Config.TAG, t.getMessage());
                    }
                });
    }

    private void addMarker(Place place) {
        LatLng userMarker = new LatLng(place.getLat(), place.getLng());

        Picasso.get().load(place.getIcon()).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                Bitmap image = createUserBitmap(bitmap);
                mMap.addMarker(new MarkerOptions().position(userMarker)
                        .title(place.getName()).icon(BitmapDescriptorFactory.fromBitmap(image))
                        .snippet(place.getVicinity()));
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                mMap.addMarker(new MarkerOptions().position(userMarker)
                        .title(place.getName()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                        .snippet(place.getVicinity()));
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });
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

//            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.avatar);
            Bitmap bitmap = bm;
            //Bitmap bitmap = BitmapFactory.decodeFile(path.toString()); /*generate bitmap here if your image comes from any url*/
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
            try {
                canvas.setBitmap(null);
            } catch (Exception e) {
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return result;
    }

    public int dp(float value) {
        if (value == 0) {
            return 0;
        }
        return (int) Math.ceil(getResources().getDisplayMetrics().density * value);
    }

    public static Bitmap createMaker(Context context, @DrawableRes int resource, String _name) {
//
//        View marker = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_layout, null);
//
//        CircleImageView markerImage = (CircleImageView) marker.findViewById(R.id.user_dp);
//        markerImage.setImageResource(resource);
//        TextView txt_name = (TextView)marker.findViewById(R.id.name);
//        txt_name.setText(_name);
//
//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//        marker.setLayoutParams(new ViewGroup.LayoutParams(52, ViewGroup.LayoutParams.WRAP_CONTENT));
//        marker.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
//        marker.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
//        marker.buildDrawingCache();
//        Bitmap bitmap = Bitmap.createBitmap(marker.getMeasuredWidth(), marker.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(bitmap);
//        marker.draw(canvas);
//
//        return bitmap;

        return null;
    }

    private Place parseJson(JsonObject object) {
        JsonObject location = object.getAsJsonObject("geometry").getAsJsonObject("location");
        double lat = location.get("lat").getAsDouble();
        double lng = location.get("lng").getAsDouble();

        String icon = object.get("icon").getAsString();
        String name = object.get("name").getAsString();
        String place_id = object.get("place_id").getAsString();
        String vicinity = object.get("vicinity").getAsString();

        Place place = new Place();
        place.setLat(lat);
        place.setLng(lng);
        place.setIcon(icon);
        place.setName(name);
        place.setPlace_id(place_id);
        place.setVicinity(vicinity);

        return place;
    }

    private void registerEventRealtime() {
        publicLocationRef = FirebaseDatabase.getInstance().getReference(Config.RF_PUBLIC_LOCATION).child(Common.currentUser.getUid());
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
        mMap.getUiSettings().setZoomControlsEnabled(true);

        LatLng hanoi = new LatLng(21.035082, 105.784361);
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.builder()
                .target(hanoi).zoom(6.5f).build()));

        Toast.makeText(this, "Map ready", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {
        mMap.clear();
        if (snapshot.getValue() != null) {
            String uid = snapshot.getKey();
            MyLocation location = snapshot.getValue(MyLocation.class);
            currentLat = location.getLatitude();
            currentLong = location.getLongitude();
            // Add Marker
            LatLng userMarker = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.addMarker(new MarkerOptions().position(userMarker)
                    .title(uid).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                    .snippet(Common.convertTimeStampToString(location.getTime())));
        }

        for (Place place : placeList) {
            LatLng userMarker = new LatLng(place.getLat(), place.getLng());
            Picasso.get().load(place.getIcon()).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    Bitmap image = createUserBitmap(bitmap);
                    mMap.addMarker(new MarkerOptions().position(userMarker)
                            .title(place.getName()).icon(BitmapDescriptorFactory.fromBitmap(image))
                            .snippet(place.getVicinity()));
                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                    mMap.addMarker(new MarkerOptions().position(userMarker)
                            .title(place.getName()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                            .snippet(place.getVicinity()));
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            });
        }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {

    }
}