package com.example.staff.api;

import com.example.staff.model.*;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.*;

public interface ApiService {

    @POST("auth/login")
    Call<ApiResponse<LoginResponse>> login(@Body LoginRequest request);

    @GET("products")
    Call<ApiResponse<PageResponse<Product>>> getProducts(
            @Query("page") int page,
            @Query("size") int size,
            @Query("name") String name
    );

    @GET("products/{id}")
    Call<ApiResponse<Product>> getProductById(@Path("id") Long id);

    @GET("products/category/{categoryId}")
    Call<ApiResponse<PageResponse<Product>>> getProductsByCategory(
            @Path("categoryId") Long categoryId,
            @Query("page") int page,
            @Query("size") int size
    );

    @GET("categories")
    Call<ApiResponse<PageResponse<Category>>> getCategories(
            @Query("page") int page,
            @Query("size") int size
    );

    @GET("orders")
    Call<ApiResponse<PageResponse<Order>>> getOrders(
            @Query("page") int page,
            @Query("size") int size
    );

    @GET("orders/user/{userId}")
    Call<ApiResponse<PageResponse<Order>>> getOrdersByUser(
            @Path("userId") Long userId,
            @Query("page") int page,
            @Query("size") int size
    );

    @GET("orders/{id}")
    Call<ApiResponse<Order>> getOrderById(@Path("id") Long id);

    @POST("orders")
    Call<ApiResponse<Order>> createOrder(@Body CreateOrderRequest request);

    @PUT("orders/{id}/status")
    Call<ApiResponse<Order>> updateOrderStatus(
            @Path("id") Long id,
            @Query("status") String status
    );

    @GET("users/{id}")
    Call<ApiResponse<User>> getUserById(@Path("id") Long id);

    @PUT("users/{id}")
    Call<ApiResponse<User>> updateUser(@Path("id") Long id, @Body User user);
}
