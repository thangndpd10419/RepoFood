package com.example.admin.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.admin.R;
import com.example.admin.model.TopProduct;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TopProductAdapter extends RecyclerView.Adapter<TopProductAdapter.TopProductViewHolder> {
    private List<TopProduct> products = new ArrayList<>();
    private final NumberFormat currencyFormat = NumberFormat.getInstance(new Locale("vi", "VN"));

    public void setProducts(List<TopProduct> products) {
        this.products = products != null ? products : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TopProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_top_product, parent, false);
        return new TopProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopProductViewHolder holder, int position) {
        holder.bind(products.get(position), position);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    class TopProductViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvRank;
        private final TextView tvProductName;
        private final TextView tvRevenue;
        private final TextView tvQuantity;
        private final TextView tvOrders;

        TopProductViewHolder(View itemView) {
            super(itemView);
            tvRank = itemView.findViewById(R.id.tvRank);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvRevenue = itemView.findViewById(R.id.tvRevenue);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvOrders = itemView.findViewById(R.id.tvOrders);
        }

        void bind(TopProduct product, int position) {
            tvRank.setText(String.valueOf(position + 1));

            if (position == 0) {
                tvRank.setBackgroundColor(Color.parseColor("#FEF3C7"));
                tvRank.setTextColor(Color.parseColor("#B45309"));
            } else if (position == 1) {
                tvRank.setBackgroundColor(Color.parseColor("#F3F4F6"));
                tvRank.setTextColor(Color.parseColor("#4B5563"));
            } else if (position == 2) {
                tvRank.setBackgroundColor(Color.parseColor("#FFEDD5"));
                tvRank.setTextColor(Color.parseColor("#C2410C"));
            } else {
                tvRank.setBackgroundColor(Color.parseColor("#FFF7ED"));
                tvRank.setTextColor(Color.parseColor("#6B7280"));
            }

            tvProductName.setText(product.getProductName());
            tvRevenue.setText(currencyFormat.format(product.getRevenue()) + " VNĐ");
            tvQuantity.setText(product.getQuantitySold() + " sp");
            tvOrders.setText(product.getOrderCount() + " đơn");
        }
    }
}
