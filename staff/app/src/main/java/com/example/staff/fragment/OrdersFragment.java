package com.example.staff.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.staff.R;
import com.example.staff.adapter.OrderAdapter;
import com.example.staff.api.RetrofitClient;
import com.example.staff.databinding.FragmentOrdersBinding;
import com.example.staff.model.ApiResponse;
import com.example.staff.model.Order;
import com.example.staff.model.PageResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrdersFragment extends Fragment {
    private FragmentOrdersBinding binding;
    private OrderAdapter orderAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentOrdersBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView();
        setupSwipeRefresh();
        loadOrders();
    }

    private void setupRecyclerView() {
        orderAdapter = new OrderAdapter();
        binding.recyclerViewOrders.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerViewOrders.setAdapter(orderAdapter);
    }

    private void setupSwipeRefresh() {
        binding.swipeRefresh.setColorSchemeResources(R.color.primary);
        binding.swipeRefresh.setOnRefreshListener(this::loadOrders);
    }

    private void loadOrders() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.layoutEmpty.setVisibility(View.GONE);

        Long userId = RetrofitClient.getUserId(requireContext());

        RetrofitClient.getApiService().getOrders(0, 100).enqueue(new Callback<ApiResponse<PageResponse<Order>>>() {
            @Override
            public void onResponse(Call<ApiResponse<PageResponse<Order>>> call,
                                   Response<ApiResponse<PageResponse<Order>>> response) {
                binding.progressBar.setVisibility(View.GONE);
                binding.swipeRefresh.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null &&
                        response.body().isSuccess() && response.body().getData() != null) {
                    List<Order> orders = response.body().getData().getContent();
                    orderAdapter.setOrders(orders);

                    binding.tvOrderCount.setText(orders.size() + " đơn hàng");
                    binding.layoutEmpty.setVisibility(orders.isEmpty() ? View.VISIBLE : View.GONE);
                    binding.recyclerViewOrders.setVisibility(orders.isEmpty() ? View.GONE : View.VISIBLE);
                } else {
                    showError("Không thể tải đơn hàng");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<PageResponse<Order>>> call, Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                binding.swipeRefresh.setRefreshing(false);
                showError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    private void showError(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
