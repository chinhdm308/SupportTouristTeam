package dmc.supporttouristteam.data.api;

import dmc.supporttouristteam.data.model.gg.Directions;
import dmc.supporttouristteam.data.model.gg.MyCurrentAddress;
import dmc.supporttouristteam.data.model.fb_mes.MyRequest;
import dmc.supporttouristteam.data.model.fb_mes.MyResponse;
import dmc.supporttouristteam.data.model.gg.NearPlacesResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface IApiCall {

    @GET("api/place/nearbysearch/json")
    Call<NearPlacesResponse> loadNearPlaces(@Query("location") String location,
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

    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAhbkdmgI:APA91bEuGqU6tzSiJUCbyq623mcK2ga9XWuxNwqnoLU624rYWsqHn-PYRwYms8yHGbag9TsyN0QlV1qWSJx7FFV6wkKbdbAmIzZ9H1Z0P1JmuPKa1snDJp67dIZtE4KAeIfMylPzS8uD"
    })
    @POST("fcm/send")
    Call<MyResponse> sendFriendRequestToUser(@Body MyRequest body);

    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAhbkdmgI:APA91bEuGqU6tzSiJUCbyq623mcK2ga9XWuxNwqnoLU624rYWsqHn-PYRwYms8yHGbag9TsyN0QlV1qWSJx7FFV6wkKbdbAmIzZ9H1Z0P1JmuPKa1snDJp67dIZtE4KAeIfMylPzS8uD"
    })
    @POST("fcm/send")
    Call<MyResponse> sendWarningMessage(@Body MyRequest body);
}
