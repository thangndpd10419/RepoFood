package com.example.admin.api;

import com.example.admin.model.*;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface ApiService {

    @POST("auth/login")
    Call<ApiResponse<LoginResponse>> login(@Body LoginRequest request);

    @POST("auth/refresh")
    Call<ApiResponse<LoginResponse>> refreshToken(@Body Map<String, String> body);

    @GET("users")
    Call<ApiResponse<PageResponse<User>>> getUsers(
            @Query("page") int page,
            @Query("size") int size
    );

    @GET("users/{id}")
    Call<ApiResponse<User>> getUserById(@Path("id") Long id);

    @PUT("users/{id}")
    Call<ApiResponse<User>> updateUser(@Path("id") Long id, @Body User user);

    @DELETE("users/{id}")
    Call<ApiResponse<Void>> deleteUser(@Path("id") Long id);

    @GET("categories")
    Call<ApiResponse<PageResponse<Category>>> getCategories(
            @Query("page") int page,
            @Query("size") int size
    );

    @GET("categories/{id}")
    Call<ApiResponse<Category>> getCategoryById(@Path("id") Long id);

    @POST("categories")
    Call<ApiResponse<Category>> createCategory(@Body Category category);

    @PUT("categories/{id}")
    Call<ApiResponse<Category>> updateCategory(@Path("id") Long id, @Body Category category);

    @DELETE("categories/{id}")
    Call<ApiResponse<Void>> deleteCategory(@Path("id") Long id);

    @GET("products")
    Call<ApiResponse<PageResponse<Product>>> getProducts(
            @Query("page") int page,
            @Query("size") int size,
            @Query("name") String name
    );

    @GET("products/{id}")
    Call<ApiResponse<Product>> getProductById(@Path("id") Long id);

    @POST("products")
    Call<ApiResponse<Product>> createProduct(@Body Product product);

    @PUT("products/{id}")
    Call<ApiResponse<Product>> updateProduct(@Path("id") Long id, @Body Product product);

    @DELETE("products/{id}")
    Call<ApiResponse<Void>> deleteProduct(@Path("id") Long id);

    @GET("orders")
    Call<ApiResponse<PageResponse<Order>>> getOrders(
            @Query("page") int page,
            @Query("size") int size
    );

    @GET("orders/{id}")
    Call<ApiResponse<Order>> getOrderById(@Path("id") Long id);

    @PUT("orders/{id}")
    Call<ApiResponse<Order>> updateOrder(@Path("id") Long id, @Body UpdateOrderRequest request);

    @DELETE("orders/{id}")
    Call<ApiResponse<Void>> deleteOrder(@Path("id") Long id);

    @GET("statistics/dashboard")
    Call<ApiResponse<DashboardStats>> getDashboardStats();

    @GET("statistics/revenue/daily")
    Call<ApiResponse<List<RevenueByDate>>> getRevenueDaily(
            @Query("startDate") String startDate,
            @Query("endDate") String endDate
    );

    @GET("statistics/revenue/monthly")
    Call<ApiResponse<List<RevenueByDate>>> getRevenueMonthly(@Query("year") int year);

    @GET("statistics/revenue/yearly")
    Call<ApiResponse<List<RevenueByDate>>> getRevenueYearly();

    @GET("statistics/revenue/category")
    Call<ApiResponse<List<RevenueByCategory>>> getRevenueByCategory();

    @GET("statistics/top-products")
    Call<ApiResponse<List<TopProduct>>> getTopProducts(@Query("limit") int limit);

    @Multipart
    @POST("upload/image")
    Call<ApiResponse<UploadResponse>> uploadImage(
            @Part MultipartBody.Part file,
            @Part("folder") RequestBody folder
    );

    @POST("users")
    Call<ApiResponse<User>> createUser(@Body UserCreateRequest request);
}
