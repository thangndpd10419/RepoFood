package com.example.admin.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.admin.R;
import com.example.admin.databinding.ItemCategoryBinding;
import com.example.admin.model.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private List<Category> categories = new ArrayList<>();
    private OnCategoryClickListener listener;

    public interface OnCategoryClickListener {
        void onEditClick(Category category);
        void onDeleteClick(Category category);
    }

    public void setOnCategoryClickListener(OnCategoryClickListener listener) {
        this.listener = listener;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories != null ? categories : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCategoryBinding binding = ItemCategoryBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new CategoryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        holder.bind(categories.get(position));
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {
        private final ItemCategoryBinding binding;

        CategoryViewHolder(ItemCategoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Category category) {
            binding.tvName.setText(category.getName() != null ? category.getName() : "N/A");
            binding.tvId.setText("ID: " + category.getId());

            String imageUrl = category.getImgCategory();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                String fullUrl = imageUrl;
                if (!imageUrl.startsWith("http://") && !imageUrl.startsWith("https://")) {
                    fullUrl = "http://10.0.2.2:8080/" + imageUrl;
                }
                Glide.with(binding.ivCategory.getContext())
                        .load(fullUrl)
                        .placeholder(R.drawable.rounded_background)
                        .error(R.drawable.rounded_background)
                        .centerCrop()
                        .into(binding.ivCategory);
            } else {
                binding.ivCategory.setImageResource(R.drawable.rounded_background);
            }

            binding.btnEdit.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEditClick(category);
                }
            });

            binding.btnDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteClick(category);
                }
            });
        }
    }
}
