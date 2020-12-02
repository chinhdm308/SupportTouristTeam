package dmc.supporttouristteam.Presenters.FindPlace;

import android.content.Context;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import dmc.supporttouristteam.Api.ApiUtils;
import dmc.supporttouristteam.Models.MyLocation;
import dmc.supporttouristteam.Models.Place;
import dmc.supporttouristteam.R;
import dmc.supporttouristteam.Utils.Common;
import dmc.supporttouristteam.Utils.Config;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FindNearbyPlacesPresenter implements FindNearbyPlacesContract.Presenter {

    private FindNearbyPlacesContract.View view;
    private FirebaseUser currentUser;
    private boolean roomCheck;
    private Context context;

    public FindNearbyPlacesPresenter(FindNearbyPlacesContract.View view, Context context) {
        this.view = view;
        this.currentUser = FirebaseAuth.getInstance().getCurrentUser();
        roomCheck = false;
        this.context = context;
    }

    @Override
    public void doSearchPlacesNearYou(MyLocation myLocation, String[] placeTypeList, int i) {
        List<Place> placeList = new ArrayList<>();
        ApiUtils.start().apiCall().loadNearPlaces(myLocation.getLatitude() + "," + myLocation.getLongitude(),
                5000, placeTypeList[i], true, "prominence",
                context.getResources().getString(R.string.google_maps_key))
                .enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if (response != null) {
                            Log.d(Config.TAG, "call api successfully");
                            JsonArray results = response.body().getAsJsonArray("results");
                            for (JsonElement elem : results) {
                                JsonObject object = elem.getAsJsonObject();
                                Place place = Common.parseJsonPlace(object);
                                placeList.add(place);
                                view.showPlace(place);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        Log.d(Config.TAG, t.getMessage());
                    }
                });
    }


}
