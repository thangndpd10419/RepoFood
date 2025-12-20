package com.example.staff.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.staff.R;
import com.example.staff.model.Product;
import com.example.staff.util.CartManager;
import com.google.android.material.button.MaterialButton;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private List<Product> products = new ArrayList<>();
    private final NumberFormat currencyFormat = NumberFormat.getInstance(new Locale("vi", "VN"));
    private static final String BASE_URL = "http://10.0.2.2:8080";

    public void setProducts(List<Product> products) {
        this.products = products != null ? products : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void updateCartQuantities() {
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        holder.bind(products.get(position));
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivProduct;
        private final TextView tvQuantityBadge;
        private final TextView tvProductName;
        private final TextView tvProductPrice;
        private final TextView tvQuantity;
        private final MaterialButton btnRemove;
        private final MaterialButton btnAdd;

        ProductViewHolder(View itemView) {
            super(itemView);
            ivProduct = itemView.findViewById(R.id.ivProduct);
            tvQuantityBadge = itemView.findViewById(R.id.tvQuantityBadge);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            btnRemove = itemView.findViewById(R.id.btnRemove);
            btnAdd = itemView.findViewById(R.id.btnAdd);
        }

        void bind(Product product) {
            tvProductName.setText(product.getName());
            tvProductPrice.setText(currencyFormat.format(product.getPrice()) + " VNĐ");

            String imageUrl = product.getImgProduct();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                if (!imageUrl.startsWith("http")) {
                    imageUrl = BASE_URL + (imageUrl.startsWith("/") ? "" : "/") + imageUrl;
                }
                Glide.with(itemView.getContext())
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_products)
                        .error(R.drawable.ic_products)
                        .centerCrop()
                        .into(ivProduct);
            } else {
                ivProduct.setImageResource(R.drawable.ic_products);
            }

            int cartQuantity = CartManager.getInstance().getQuantityForProduct(product.getId());
            tvQuantity.setText(String.valueOf(cartQuantity));

            if (cartQuantity > 0) {
                tvQuantityBadge.setVisibility(View.VISIBLE);
                tvQuantityBadge.setText(String.valueOf(cartQuantity));
            } else {
                tvQuantityBadge.setVisibility(View.GONE);
            }

            btnAdd.setOnClickListener(v -> {
                CartManager.getInstance().addToCart(product);
                notifyItemChanged(getAdapterPosition());
            });

            btnRemove.setOnClickListener(v -> {
                CartManager.getInstance().decreaseQuantity(product);
                notifyItemChanged(getAdapterPosition());
            });
        }
    }
}
