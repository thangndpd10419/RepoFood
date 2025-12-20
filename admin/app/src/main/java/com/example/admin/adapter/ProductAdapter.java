package com.example.admin.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.admin.R;
import com.example.admin.databinding.ItemProductBinding;
import com.example.admin.model.Product;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private List<Product> products = new ArrayList<>();
    private OnProductClickListener listener;
    private final NumberFormat currencyFormat;

    public ProductAdapter() {
        currencyFormat = NumberFormat.getInstance(new Locale("vi", "VN"));
    }

    public interface OnProductClickListener {
        void onEditClick(Product product);
        void onDeleteClick(Product product);
    }

    public void setOnProductClickListener(OnProductClickListener listener) {
        this.listener = listener;
    }

    public void setProducts(List<Product> products) {
        this.products = products != null ? products : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemProductBinding binding = ItemProductBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ProductViewHolder(binding);
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
        private final ItemProductBinding binding;

        ProductViewHolder(ItemProductBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Product product) {
            binding.tvName.setText(product.getName() != null ? product.getName() : "N/A");

            if (product.getPrice() != null) {
                binding.tvPrice.setText(currencyFormat.format(product.getPrice()) + " VND");
            } else {
                binding.tvPrice.setText("0 VND");
            }

            binding.tvQuantity.setText("So luong: " +
                    (product.getQuantity() != null ? product.getQuantity() : 0));

            binding.tvCategory.setText(product.getCategoryName() != null ?
                    product.getCategoryName() : "Chua phan loai");

            String imageUrl = product.getImgProduct();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                String fullUrl = imageUrl;
                if (!imageUrl.startsWith("http://") && !imageUrl.startsWith("https://")) {
                    fullUrl = "http://10.0.2.2:8080/" + imageUrl;
                }
                Glide.with(binding.ivProduct.getContext())
                        .load(fullUrl)
                        .placeholder(R.drawable.rounded_background)
                        .error(R.drawable.rounded_background)
                        .centerCrop()
                        .into(binding.ivProduct);
            } else {
                binding.ivProduct.setImageResource(R.drawable.rounded_background);
            }

            binding.btnEdit.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEditClick(product);
                }
            });

            binding.btnDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteClick(product);
                }
            });
        }
    }
}
