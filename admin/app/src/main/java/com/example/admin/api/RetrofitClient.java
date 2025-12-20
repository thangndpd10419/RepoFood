package com.example.admin.api;

import android.content.Context;
import android.content.SharedPreferences;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.TimeUnit;

public class RetrofitClient {
    private static final String BASE_URL = "http://10.0.2.2:8080/api/";
    private static Retrofit retrofit = null;
    private static ApiService apiService = null;
    private static String authToken = null;

    public static void setAuthToken(String token) {
        authToken = token;
        retrofit = null;
        apiService = null;
    }

    public static String getAuthToken() {
        return authToken;
    }

    public static void clearAuthToken() {
        authToken = null;
        retrofit = null;
        apiService = null;
    }

    public static Retrofit getRetrofit() {
        if (retrofit == null) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            Interceptor authInterceptor = chain -> {
                Request original = chain.request();
                Request.Builder builder = original.newBuilder()
                        .header("Content-Type", "application/json");

                if (authToken != null && !authToken.isEmpty()) {
                    builder.header("Authorization", "Bearer " + authToken);
                }

                Request request = builder.method(original.method(), original.body()).build();
                return chain.proceed(request);
            };

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(authInterceptor)
                    .addInterceptor(loggingInterceptor)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static ApiService getApiService() {
        if (apiService == null) {
            apiService = getRetrofit().create(ApiService.class);
        }
        return apiService;
    }

    public static void saveToken(Context context, String accessToken, String refreshToken, Long userId) {
        SharedPreferences prefs = context.getSharedPreferences("FoodBeAdmin", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("accessToken", accessToken);
        editor.putString("refreshToken", refreshToken);
        editor.putLong("userId", userId != null ? userId : 0);
        editor.apply();
        setAuthToken(accessToken);
    }

    public static void loadToken(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("FoodBeAdmin", Context.MODE_PRIVATE);
        String token = prefs.getString("accessToken", null);
        if (token != null) {
            setAuthToken(token);
        }
    }

    public static Long getUserId(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("FoodBeAdmin", Context.MODE_PRIVATE);
        return prefs.getLong("userId", 0);
    }

    public static void clearToken(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("FoodBeAdmin", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
        clearAuthToken();
    }

    public static boolean isLoggedIn(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("FoodBeAdmin", Context.MODE_PRIVATE);
        return prefs.getString("accessToken", null) != null;
    }
}
