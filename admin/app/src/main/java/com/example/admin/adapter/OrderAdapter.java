package com.example.admin.adapter;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.admin.databinding.ItemOrderBinding;
import com.example.admin.model.Order;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private List<Order> orders = new ArrayList<>();
    private final NumberFormat currencyFormat;
    private OnOrderClickListener listener;

    public interface OnOrderClickListener {
        void onOrderClick(Order order);
    }

    public OrderAdapter(OnOrderClickListener listener) {
        this.listener = listener;
        currencyFormat = NumberFormat.getInstance(new Locale("vi", "VN"));
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders != null ? orders : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemOrderBinding binding = ItemOrderBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new OrderViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        holder.bind(orders.get(position));
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    class OrderViewHolder extends RecyclerView.ViewHolder {
        private final ItemOrderBinding binding;

        OrderViewHolder(ItemOrderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Order order) {
            binding.tvOrderId.setText("Đơn hàng #" + order.getId());

            if (order.getCreatedAt() != null) {
                String date = order.getCreatedAt();
                if (date.length() > 10) {
                    date = date.substring(0, 10);
                }
                binding.tvOrderDate.setText(date);
            } else {
                binding.tvOrderDate.setText("N/A");
            }

            if (order.getTotalPrice() != null) {
                binding.tvOrderPrice.setText(currencyFormat.format(order.getTotalPrice()) + " VNĐ");
            } else {
                binding.tvOrderPrice.setText("0 VNĐ");
            }

            String status = order.getStatus() != null ? order.getStatus() : "UNKNOWN";
            binding.tvOrderStatus.setText(getStatusText(status));

            GradientDrawable drawable = (GradientDrawable) binding.tvOrderStatus.getBackground();
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

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) {
                    listener.onOrderClick(order);
                }
            });
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
    }
}
