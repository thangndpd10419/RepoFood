package com.example.staff.adapter;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.staff.R;
import com.example.staff.model.Order;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private List<Order> orders = new ArrayList<>();
    private final NumberFormat currencyFormat = NumberFormat.getInstance(new Locale("vi", "VN"));

    public void setOrders(List<Order> orders) {
        this.orders = orders != null ? orders : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
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
        private final TextView tvOrderId;
        private final TextView tvStatus;
        private final TextView tvTableInfo;
        private final TextView tvOrderItems;
        private final TextView tvOrderTime;
        private final TextView tvTotalPrice;

        OrderViewHolder(View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvTableInfo = itemView.findViewById(R.id.tvTableInfo);
            tvOrderItems = itemView.findViewById(R.id.tvOrderItems);
            tvOrderTime = itemView.findViewById(R.id.tvOrderTime);
            tvTotalPrice = itemView.findViewById(R.id.tvTotalPrice);
        }

        void bind(Order order) {
            tvOrderId.setText(String.format(Locale.getDefault(), "Đơn #%d", order.getId()));
            tvTableInfo.setText(order.getTable() != null ? "Bàn " + order.getTable() : "Không có bàn");

            int itemCount = order.getOrderDetails() != null ? order.getOrderDetails().size() : 0;
            tvOrderItems.setText(itemCount + " sản phẩm");

            tvTotalPrice.setText(currencyFormat.format(order.getTotalPrice()) + " VNĐ");

            String createdAt = order.getCreatedAt();
            if (createdAt != null && createdAt.length() >= 16) {
                tvOrderTime.setText(createdAt.substring(11, 16));
            } else {
                tvOrderTime.setText("");
            }

            String status = order.getStatus();
            String statusText;
            int statusColor;

            if (status == null) status = "PENDING";

            switch (status.toUpperCase()) {
                case "CONFIRMED":
                    statusText = "Đã xác nhận";
                    statusColor = Color.parseColor("#3B82F6");
                    break;
                case "PROCESSING":
                    statusText = "Đang xử lý";
                    statusColor = Color.parseColor("#8B5CF6");
                    break;
                case "COMPLETED":
                case "DELIVERED":
                    statusText = "Hoàn thành";
                    statusColor = Color.parseColor("#10B981");
                    break;
                case "CANCELLED":
                    statusText = "Đã hủy";
                    statusColor = Color.parseColor("#EF4444");
                    break;
                default:
                    statusText = "Chờ xác nhận";
                    statusColor = Color.parseColor("#F59E0B");
                    break;
            }

            tvStatus.setText(statusText);
            GradientDrawable background = new GradientDrawable();
            background.setColor(statusColor);
            background.setCornerRadius(24f);
            tvStatus.setBackground(background);
        }
    }
}
