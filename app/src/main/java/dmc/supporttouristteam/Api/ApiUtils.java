package dmc.supporttouristteam.Api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiUtils {
    private static final String BASE_URL = "https://maps.googleapis.com/maps/";

    private static ApiUtils instance = null;
    private final IApiCall iApiCallInterface;
    private static Retrofit retrofit = null;

    private ApiUtils() {
        OkHttpClient builder = new OkHttpClient.Builder()
                .readTimeout(5000, TimeUnit.MILLISECONDS)
                .writeTimeout(5000, TimeUnit.MILLISECONDS)
                .connectTimeout(10000, TimeUnit.MILLISECONDS)
                .retryOnConnectionFailure(true)
                .build();

        Gson gson = new GsonBuilder().setLenient().create();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(builder)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        iApiCallInterface = retrofit.create(IApiCall.class);
    }

    public static ApiUtils start() {
        return instance = instance == null ? new ApiUtils() : instance;
    }

    public IApiCall apiCall() {
        return iApiCallInterface;
    }
}
