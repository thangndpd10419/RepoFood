package com.example.staff.activity;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.staff.R;
import com.example.staff.databinding.ActivityMainBinding;
import com.example.staff.fragment.CartFragment;
import com.example.staff.fragment.OrdersFragment;
import com.example.staff.fragment.ProductFragment;
import com.example.staff.fragment.ProfileFragment;
import com.example.staff.fragment.TableFragment;
import com.example.staff.util.CartManager;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity implements CartManager.CartChangeListener {
    private ActivityMainBinding binding;
    private BadgeDrawable cartBadge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupBottomNavigation();
        setupCartBadge();

        if (savedInstanceState == null) {
            loadFragment(new TableFragment());
        }

        CartManager.getInstance().addListener(this);
    }

    private void setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;
                int itemId = item.getItemId();

                if (itemId == R.id.nav_tables) {
                    fragment = new TableFragment();
                } else if (itemId == R.id.nav_products) {
                    fragment = new ProductFragment();
                } else if (itemId == R.id.nav_cart) {
                    fragment = new CartFragment();
                } else if (itemId == R.id.nav_orders) {
                    fragment = new OrdersFragment();
                } else if (itemId == R.id.nav_profile) {
                    fragment = new ProfileFragment();
                }

                if (fragment != null) {
                    loadFragment(fragment);
                    return true;
                }
                return false;
            }
        });
    }

    private void setupCartBadge() {
        cartBadge = binding.bottomNavigation.getOrCreateBadge(R.id.nav_cart);
        cartBadge.setBackgroundColor(getResources().getColor(R.color.error, null));
        updateCartBadge();
    }

    private void updateCartBadge() {
        int count = CartManager.getInstance().getCartItemCount();
        if (count > 0) {
            cartBadge.setVisible(true);
            cartBadge.setNumber(count);
        } else {
            cartBadge.setVisible(false);
        }
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }

    public void navigateToProducts() {
        binding.bottomNavigation.setSelectedItemId(R.id.nav_products);
    }

    public void navigateToCart() {
        binding.bottomNavigation.setSelectedItemId(R.id.nav_cart);
    }

    public void navigateToOrders() {
        binding.bottomNavigation.setSelectedItemId(R.id.nav_orders);
    }

    @Override
    public void onCartChanged() {
        runOnUiThread(this::updateCartBadge);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CartManager.getInstance().removeListener(this);
    }
}
