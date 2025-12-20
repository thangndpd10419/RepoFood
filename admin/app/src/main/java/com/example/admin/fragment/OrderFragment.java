package com.example.admin.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.admin.R;
import com.example.admin.adapter.OrderAdapter;
import com.example.admin.adapter.OrderDetailAdapter;
import com.example.admin.api.RetrofitClient;
import com.example.admin.databinding.DialogOrderDetailBinding;
import com.example.admin.databinding.FragmentOrderBinding;
import com.example.admin.model.ApiResponse;
import com.example.admin.model.Order;
import com.example.admin.model.PageResponse;
import com.example.admin.model.UpdateOrderRequest;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderFragment extends Fragment implements OrderAdapter.OnOrderClickListener {
    private FragmentOrderBinding binding;
    private OrderAdapter orderAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentOrderBinding.inflate(inflater, container, false);
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
        orderAdapter = new OrderAdapter(this);
        binding.recyclerViewOrders.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerViewOrders.setAdapter(orderAdapter);
    }

    @Override
    public void onOrderClick(Order order) {
        showOrderDetailDialog(order);
    }

    private void showOrderDetailDialog(Order order) {
        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        DialogOrderDetailBinding dialogBinding = DialogOrderDetailBinding.inflate(getLayoutInflater());
        dialog.setContentView(dialogBinding.getRoot());

        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.bg_dialog);
        }

        NumberFormat currencyFormat = NumberFormat.getInstance(new Locale("vi", "VN"));

        dialogBinding.tvOrderTitle.setText("Đơn hàng #" + order.getId());
        dialogBinding.tvTableNumber.setText(order.getTable() != null ? String.valueOf(order.getTable()) : "N/A");
        dialogBinding.tvStaffName.setText(order.getUserName() != null ? order.getUserName() : "N/A");

        if (order.getCreatedAt() != null) {
            String dateTime = order.getCreatedAt();
            if (dateTime.contains("T")) {
                dateTime = dateTime.replace("T", " ");
                if (dateTime.length() > 16) {
                    dateTime = dateTime.substring(0, 16);
                }
            }
            dialogBinding.tvOrderTime.setText(dateTime);
        } else {
            dialogBinding.tvOrderTime.setText("N/A");
        }

        if (order.getNote() != null && !order.getNote().isEmpty()) {
            dialogBinding.layoutNote.setVisibility(View.VISIBLE);
            dialogBinding.tvNote.setText(order.getNote());
        } else {
            dialogBinding.layoutNote.setVisibility(View.GONE);
        }

        String status = order.getStatus() != null ? order.getStatus() : "UNKNOWN";
        dialogBinding.tvOrderStatus.setText(getStatusText(status));
        GradientDrawable drawable = (GradientDrawable) dialogBinding.tvOrderStatus.getBackground();
        setStatusColor(drawable, status);

        if (order.getTotalPrice() != null) {
            dialogBinding.tvTotalPrice.setText(currencyFormat.format(order.getTotalPrice()) + " VNĐ");
        } else {
            dialogBinding.tvTotalPrice.setText("0 VNĐ");
        }

        OrderDetailAdapter detailAdapter = new OrderDetailAdapter();
        dialogBinding.recyclerViewItems.setLayoutManager(new LinearLayoutManager(requireContext()));
        dialogBinding.recyclerViewItems.setAdapter(detailAdapter);
        if (order.getOrderDetails() != null) {
            detailAdapter.setItems(order.getOrderDetails());
        }

        dialogBinding.btnClose.setOnClickListener(v -> dialog.dismiss());
        dialogBinding.btnUpdateStatus.setOnClickListener(v -> {
            dialog.dismiss();
            showStatusUpdateDialog(order);
        });

        dialog.show();
    }

    private void showStatusUpdateDialog(Order order) {
        String[] statusOptions = {"PENDING", "PROCESSING", "COMPLETED", "CANCELLED"};
        String[] statusLabels = {"Chờ xử lý", "Đang xử lý", "Hoàn thành", "Đã hủy"};

        int currentIndex = 0;
        for (int i = 0; i < statusOptions.length; i++) {
            if (statusOptions[i].equalsIgnoreCase(order.getStatus())) {
                currentIndex = i;
                break;
            }
        }

        new AlertDialog.Builder(requireContext())
                .setTitle("Cập nhật trạng thái")
                .setSingleChoiceItems(statusLabels, currentIndex, null)
                .setPositiveButton("Cập nhật", (dialogInterface, which) -> {
                    int selectedPosition = ((AlertDialog) dialogInterface).getListView().getCheckedItemPosition();
                    String newStatus = statusOptions[selectedPosition];
                    updateOrderStatus(order.getId(), newStatus);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private String getStatusText(String status) {
        switch (status.toUpperCase()) {
            case "PENDING": return "Chờ xử lý";
            case "PROCESSING": return "Đang xử lý";
            case "COMPLETED": return "Hoàn thành";
            case "CANCELLED": return "Đã hủy";
            default: return status;
        }
    }

    private void setStatusColor(GradientDrawable drawable, String status) {
        switch (status.toUpperCase()) {
            case "COMPLETED":
                drawable.setColor(Color.parseColor("#4CAF50"));
                break;
            case "PROCESSING":
                drawable.setColor(Color.parseColor("#2196F3"));
                break;
            case "PENDING":
                drawable.setColor(Color.parseColor("#FF9800"));
                break;
            case "CANCELLED":
                drawable.setColor(Color.parseColor("#F44336"));
                break;
            default:
                drawable.setColor(Color.parseColor("#9E9E9E"));
                break;
        }
    }

    private void updateOrderStatus(Long orderId, String newStatus) {
        UpdateOrderRequest request = new UpdateOrderRequest(newStatus);

        RetrofitClient.getApiService().updateOrder(orderId, request).enqueue(new Callback<ApiResponse<Order>>() {
            @Override
            public void onResponse(Call<ApiResponse<Order>> call, Response<ApiResponse<Order>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(getContext(), "Cập nhật trạng thái thành công", Toast.LENGTH_SHORT).show();
                    loadOrders();
                } else {
                    String error = response.body() != null ? response.body().getMessage() : "Cập nhật thất bại";
                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Order>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupSwipeRefresh() {
        binding.swipeRefresh.setColorSchemeResources(R.color.primary);
        binding.swipeRefresh.setOnRefreshListener(this::loadOrders);
    }

    private void loadOrders() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.layoutEmpty.setVisibility(View.GONE);

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
