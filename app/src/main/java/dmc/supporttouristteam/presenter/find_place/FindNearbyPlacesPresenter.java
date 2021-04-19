package dmc.supporttouristteam.presenter.find_place;

import android.content.Context;
import android.util.Log;

import dmc.supporttouristteam.R;
import dmc.supporttouristteam.data.api.ApiUtils;
import dmc.supporttouristteam.data.model.fb.PublicLocation;
import dmc.supporttouristteam.data.model.gg.NearPlacesResponse;
import dmc.supporttouristteam.data.model.Place;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FindNearbyPlacesPresenter implements FindNearbyPlacesContract.Presenter {

    private static final String TAG = "tagFindNearbyPlacesPresenter";
    private FindNearbyPlacesContract.View view;
    private Context context;

    public FindNearbyPlacesPresenter(FindNearbyPlacesContract.View view, Context context) {
        this.view = view;
        this.context = context;
    }

    @Override
    public void doSearchPlacesNearYou(PublicLocation publicLocation, String[] placeTypeList, int i) {
        ApiUtils.start(ApiUtils.BASE_URL_GOOGLE).apiCall().loadNearPlaces(publicLocation.getLatitude() + "," + publicLocation.getLongitude(),
                5000, placeTypeList[i], true, "prominence",
                context.getResources().getString(R.string.google_maps_key))
                .enqueue(new Callback<NearPlacesResponse>() {
                    @Override
                    public void onResponse(Call<NearPlacesResponse> call, Response<NearPlacesResponse> response) {
                        if (response != null) {
                            if (response.code() == 200) {
                                Log.d(TAG, "call api successfully");
                                Log.d(TAG, response.raw().request().url().toString());
                                for(NearPlacesResponse.Result i : response.body().results) {
                                    Place place = new Place();
                                    place.setIcon(i.icon);
                                    place.setLat(i.geometry.location.lat);
                                    place.setLng(i.geometry.location.lng);
                                    place.setName(i.name);
                                    place.setPlace_id(i.place_id);
                                    place.setVicinity(i.vicinity);
                                    view.showPlace(place);
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<NearPlacesResponse> call, Throwable t) {
                        Log.d(TAG, t.getMessage());
                    }
                });
    }

}
