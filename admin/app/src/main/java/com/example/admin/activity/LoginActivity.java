package com.example.admin.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.admin.api.RetrofitClient;
import com.example.admin.databinding.ActivityLoginBinding;
import com.example.admin.model.ApiResponse;
import com.example.admin.model.LoginRequest;
import com.example.admin.model.LoginResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (RetrofitClient.isLoggedIn(this)) {
            RetrofitClient.loadToken(this);
            navigateToMain();
            return;
        }

        setupListeners();
    }

    private void setupListeners() {
        binding.btnLogin.setOnClickListener(v -> performLogin());
    }

    private void performLogin() {
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        if (email.isEmpty()) {
            binding.etEmail.setError("Vui lòng nhập email");
            binding.etEmail.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            binding.etPassword.setError("Vui lòng nhập mật khẩu");
            binding.etPassword.requestFocus();
            return;
        }

        showLoading(true);

        LoginRequest request = new LoginRequest(email, password);
        RetrofitClient.getApiService().login(request).enqueue(new Callback<ApiResponse<LoginResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<LoginResponse>> call, Response<ApiResponse<LoginResponse>> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<LoginResponse> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        LoginResponse data = apiResponse.getData();

                        android.util.Log.d("LOGIN_DEBUG", "Role from server: " + data.getRole());
                        android.util.Log.d("LOGIN_DEBUG", "Email from server: " + data.getEmail());

                        if (data.getRole() == null || !"ADMIN".equalsIgnoreCase(data.getRole())) {
                            Toast.makeText(LoginActivity.this,
                                    "Chỉ tài khoản ADMIN mới có quyền truy cập! (Role: " + data.getRole() + ")",
                                    Toast.LENGTH_LONG).show();
                            return;
                        }

                        RetrofitClient.saveToken(
                                LoginActivity.this,
                                data.getAccessToken(),
                                data.getRefreshToken(),
                                data.getUserId()
                        );

                        Toast.makeText(LoginActivity.this,
                                "Đăng nhập thành công!",
                                Toast.LENGTH_SHORT).show();

                        navigateToMain();
                    } else {
                        Toast.makeText(LoginActivity.this,
                                apiResponse.getMessage() != null ? apiResponse.getMessage() : "Đăng nhập thất bại",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this,
                            "Lỗi: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<LoginResponse>> call, Throwable t) {
                showLoading(false);
                Toast.makeText(LoginActivity.this,
                        "Lỗi kết nối: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLoading(boolean show) {
        binding.progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        binding.btnLogin.setEnabled(!show);
    }

    private void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
