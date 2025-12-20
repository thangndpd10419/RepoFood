package com.example.staff.fragment;

import android.app.AlertDialog;
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
import com.example.staff.activity.MainActivity;
import com.example.staff.adapter.CartAdapter;
import com.example.staff.api.RetrofitClient;
import com.example.staff.databinding.FragmentCartBinding;
import com.example.staff.model.ApiResponse;
import com.example.staff.model.CartItem;
import com.example.staff.model.CreateOrderRequest;
import com.example.staff.model.Order;
import com.example.staff.util.CartManager;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartFragment extends Fragment implements CartAdapter.OnCartChangeListener {
    private FragmentCartBinding binding;
    private CartAdapter cartAdapter;
    private final NumberFormat currencyFormat = NumberFormat.getInstance(new Locale("vi", "VN"));

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentCartBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView();
        setupListeners();
        updateUI();
    }

    private void setupRecyclerView() {
        cartAdapter = new CartAdapter(this);
        binding.recyclerViewCart.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerViewCart.setAdapter(cartAdapter);
    }

    private void setupListeners() {
        binding.btnClearCart.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Xóa giỏ hàng")
                    .setMessage("Bạn có chắc muốn xóa tất cả sản phẩm trong giỏ?")
                    .setPositiveButton(R.string.yes, (dialog, which) -> {
                        CartManager.getInstance().clearCart();
                        updateUI();
                    })
                    .setNegativeButton(R.string.no, null)
                    .show();
        });

        binding.btnBrowseProducts.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).navigateToProducts();
            }
        });

        binding.btnPlaceOrder.setOnClickListener(v -> placeOrder());
    }

    private void updateUI() {
        List<CartItem> items = CartManager.getInstance().getCartItems();
        cartAdapter.setCartItems(items);

        boolean isEmpty = items.isEmpty();
        binding.layoutEmpty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        binding.recyclerViewCart.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        binding.cardSummary.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        binding.tilNote.setVisibility(isEmpty ? View.GONE : View.VISIBLE);

        Integer table = CartManager.getInstance().getSelectedTable();
        if (table != null && table > 0) {
            binding.tvTableInfo.setText("Bàn: " + table);
        } else {
            binding.tvTableInfo.setText("Bàn: Chưa chọn");
        }

        double total = CartManager.getInstance().getTotalPrice();
        binding.tvTotalPrice.setText(currencyFormat.format(total) + " VNĐ");
    }

    private void placeOrder() {
        Integer table = CartManager.getInstance().getSelectedTable();
        if (table == null || table <= 0) {
            Toast.makeText(getContext(), R.string.select_table_first, Toast.LENGTH_SHORT).show();
            return;
        }

        List<CartItem> items = CartManager.getInstance().getCartItems();
        if (items.isEmpty()) {
            Toast.makeText(getContext(), R.string.cart_empty, Toast.LENGTH_SHORT).show();
            return;
        }

        List<CreateOrderRequest.OrderDetailRequest> orderDetails = new ArrayList<>();
        for (CartItem item : items) {
            orderDetails.add(new CreateOrderRequest.OrderDetailRequest(
                    item.getProduct().getId(),
                    item.getQuantity()
            ));
        }

        String note = binding.etNote.getText() != null ? binding.etNote.getText().toString().trim() : "";

        CreateOrderRequest request = new CreateOrderRequest(null, table, note, orderDetails);

        binding.btnPlaceOrder.setEnabled(false);
        binding.btnPlaceOrder.setText("Đang đặt hàng...");

        RetrofitClient.getApiService().createOrder(request).enqueue(new Callback<ApiResponse<Order>>() {
            @Override
            public void onResponse(Call<ApiResponse<Order>> call, Response<ApiResponse<Order>> response) {
                binding.btnPlaceOrder.setEnabled(true);
                binding.btnPlaceOrder.setText(R.string.place_order);

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(getContext(), R.string.order_success, Toast.LENGTH_SHORT).show();
                    CartManager.getInstance().clearCart();
                    updateUI();

                    if (getActivity() instanceof MainActivity) {
                        ((MainActivity) getActivity()).navigateToOrders();
                    }
                } else {
                    String error = response.body() != null ? response.body().getMessage() : getString(R.string.order_failed);
                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Order>> call, Throwable t) {
                binding.btnPlaceOrder.setEnabled(true);
                binding.btnPlaceOrder.setText(R.string.place_order);
                Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onCartChanged() {
        updateUI();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
