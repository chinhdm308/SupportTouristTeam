package dmc.supporttouristteam.Api;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface AppApi {
    @GET("api/place/nearbysearch/json")
    Call<JsonObject> loadNearPlaces(@Query("location") String location,
                                  @Query("radius") int radius,
                                  @Query("type") String type,
                                  @Query("sensor") boolean sensor,
                                  @Query("rankby") String rankby,
                                  @Query("key") String key);
}
