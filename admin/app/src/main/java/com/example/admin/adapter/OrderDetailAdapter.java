package com.example.admin.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.admin.databinding.ItemOrderDetailBinding;
import com.example.admin.model.Order;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.ViewHolder> {
    private List<Order.OrderDetail> items = new ArrayList<>();
    private final NumberFormat currencyFormat;

    public OrderDetailAdapter() {
        currencyFormat = NumberFormat.getInstance(new Locale("vi", "VN"));
    }

    public void setItems(List<Order.OrderDetail> items) {
        this.items = items != null ? items : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemOrderDetailBinding binding = ItemOrderDetailBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemOrderDetailBinding binding;

        ViewHolder(ItemOrderDetailBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Order.OrderDetail item) {
            String name = item.getProductName() != null ? item.getProductName() : "Sản phẩm";
            binding.tvProductName.setText(name);
            binding.tvQuantity.setText("x" + item.getQuantity());

            if (item.getPrice() != null) {
                double totalItemPrice = item.getPrice() * item.getQuantity();
                binding.tvPrice.setText(currencyFormat.format(totalItemPrice) + "đ");
            } else {
                binding.tvPrice.setText("0đ");
            }
        }
    }
}
