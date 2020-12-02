package dmc.supporttouristteam.Api;

import com.google.gson.JsonObject;

import dmc.supporttouristteam.Models.Directions;
import dmc.supporttouristteam.Models.MyCurrentAddress;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IApiCall {
    @GET("api/place/nearbysearch/json")
    Call<JsonObject> loadNearPlaces(@Query("location") String location,
                                    @Query("radius") int radius,
                                    @Query("type") String type,
                                    @Query("sensor") boolean sensor,
                                    @Query("rankby") String rankby,
                                    @Query("key") String key);


    @GET("api/directions/json")
    Call<Directions> loadDirect(@Query("origin") String origin,
                                @Query("destination") String destination,
                                @Query("key") String key);

    @GET("api/geocode/json")
    Call<MyCurrentAddress> loadCurrentAddress(@Query("latlng") String latlng,
                                              @Query("key") String key);
}
