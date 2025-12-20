package com.example.staff.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.staff.R;
import com.example.staff.adapter.CategoryChipAdapter;
import com.example.staff.adapter.ProductAdapter;
import com.example.staff.api.RetrofitClient;
import com.example.staff.databinding.FragmentProductBinding;
import com.example.staff.model.ApiResponse;
import com.example.staff.model.Category;
import com.example.staff.model.PageResponse;
import com.example.staff.model.Product;
import com.example.staff.util.CartManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductFragment extends Fragment implements
        CategoryChipAdapter.OnCategorySelectedListener,
        CartManager.CartChangeListener {

    private FragmentProductBinding binding;
    private ProductAdapter productAdapter;
    private CategoryChipAdapter categoryChipAdapter;
    private Long selectedCategoryId = null;
    private String searchQuery = "";
    private Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentProductBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerViews();
        setupSearch();
        setupSwipeRefresh();
        loadCategories();
        loadProducts();

        CartManager.getInstance().addListener(this);
    }

    private void setupRecyclerViews() {
        categoryChipAdapter = new CategoryChipAdapter(this);
        binding.recyclerViewCategories.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.recyclerViewCategories.setAdapter(categoryChipAdapter);

        productAdapter = new ProductAdapter();
        binding.recyclerViewProducts.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        binding.recyclerViewProducts.setAdapter(productAdapter);
    }

    private void setupSearch() {
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (searchRunnable != null) {
                    searchHandler.removeCallbacks(searchRunnable);
                }
                searchRunnable = () -> {
                    searchQuery = s.toString().trim();
                    loadProducts();
                };
                searchHandler.postDelayed(searchRunnable, 500);
            }
        });
    }

    private void setupSwipeRefresh() {
        binding.swipeRefresh.setColorSchemeResources(R.color.primary);
        binding.swipeRefresh.setOnRefreshListener(() -> {
            loadCategories();
            loadProducts();
        });
    }

    private void loadCategories() {
        RetrofitClient.getApiService().getCategories(0, 100).enqueue(new Callback<ApiResponse<PageResponse<Category>>>() {
            @Override
            public void onResponse(Call<ApiResponse<PageResponse<Category>>> call,
                                   Response<ApiResponse<PageResponse<Category>>> response) {
                if (response.isSuccessful() && response.body() != null &&
                        response.body().isSuccess() && response.body().getData() != null) {
                    List<Category> categories = response.body().getData().getContent();
                    categoryChipAdapter.setCategories(categories);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<PageResponse<Category>>> call, Throwable t) {
            }
        });
    }

    private void loadProducts() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.tvEmpty.setVisibility(View.GONE);

        String search = searchQuery.isEmpty() ? null : searchQuery;

        RetrofitClient.getApiService().getProducts(0, 100, search).enqueue(new Callback<ApiResponse<PageResponse<Product>>>() {
            @Override
            public void onResponse(Call<ApiResponse<PageResponse<Product>>> call,
                                   Response<ApiResponse<PageResponse<Product>>> response) {
                binding.progressBar.setVisibility(View.GONE);
                binding.swipeRefresh.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null &&
                        response.body().isSuccess() && response.body().getData() != null) {
                    List<Product> products = response.body().getData().getContent();

                    if (selectedCategoryId != null) {
                        products.removeIf(p -> !selectedCategoryId.equals(p.getCategoryId()));
                    }

                    productAdapter.setProducts(products);
                    binding.tvEmpty.setVisibility(products.isEmpty() ? View.VISIBLE : View.GONE);
                } else {
                    showError("Không thể tải sản phẩm");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<PageResponse<Product>>> call, Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                binding.swipeRefresh.setRefreshing(false);
                showError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    @Override
    public void onCategorySelected(Long categoryId) {
        selectedCategoryId = categoryId;
        loadProducts();
    }

    @Override
    public void onCartChanged() {
        if (isAdded()) {
            requireActivity().runOnUiThread(() -> productAdapter.updateCartQuantities());
        }
    }

    private void showError(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        CartManager.getInstance().removeListener(this);
        if (searchRunnable != null) {
            searchHandler.removeCallbacks(searchRunnable);
        }
        binding = null;
    }
}
