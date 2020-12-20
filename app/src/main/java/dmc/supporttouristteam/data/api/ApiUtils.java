package dmc.supporttouristteam.data.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiUtils {
    public static final String BASE_URL_GOOGLE = "https://maps.googleapis.com/maps/";
    public static final String BASE_URL_FIREBASE = "https://fcm.googleapis.com/";

    private static ApiUtils instance;
    private final IApiCall iApiCallInterface;

    private ApiUtils(String baseURL) {
        OkHttpClient builder = new OkHttpClient.Builder()
                .readTimeout(5000, TimeUnit.MILLISECONDS)
                .writeTimeout(5000, TimeUnit.MILLISECONDS)
                .connectTimeout(10000, TimeUnit.MILLISECONDS)
                .retryOnConnectionFailure(true)
                .build();

        Gson gson = new GsonBuilder().setLenient().create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseURL)
                .client(builder)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        iApiCallInterface = retrofit.create(IApiCall.class);
    }

    public static ApiUtils start(String baseURL) {
        return instance = instance == null ? new ApiUtils(baseURL) : instance;
    }

    public IApiCall apiCall() {
        return iApiCallInterface;
    }
}
