package com.example.admin.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.admin.R;
import com.example.admin.api.RetrofitClient;
import com.example.admin.databinding.ActivityMainBinding;
import com.example.admin.fragment.CategoryFragment;
import com.example.admin.fragment.OrderFragment;
import com.example.admin.fragment.ProductFragment;
import com.example.admin.fragment.RevenueFragment;
import com.example.admin.fragment.UserFragment;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupBottomNavigation();
        setupLogoutButton();

        if (savedInstanceState == null) {
            loadFragment(new UserFragment());
        }
    }

    private void setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment fragment = null;

            int itemId = item.getItemId();
            if (itemId == R.id.nav_users) {
                fragment = new UserFragment();
            } else if (itemId == R.id.nav_categories) {
                fragment = new CategoryFragment();
            } else if (itemId == R.id.nav_products) {
                fragment = new ProductFragment();
            } else if (itemId == R.id.nav_orders) {
                fragment = new OrderFragment();
            } else if (itemId == R.id.nav_revenue) {
                fragment = new RevenueFragment();
            }

            if (fragment != null) {
                loadFragment(fragment);
            }
            return true;
        });
    }

    private void setupLogoutButton() {
        binding.btnLogout.setOnClickListener(v -> showLogoutDialog());
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất?")
                .setPositiveButton("Đăng xuất", (dialog, which) -> {
                    RetrofitClient.clearToken(this);
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
