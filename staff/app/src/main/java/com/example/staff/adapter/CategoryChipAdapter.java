package com.example.staff.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.staff.R;
import com.example.staff.model.Category;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

public class CategoryChipAdapter extends RecyclerView.Adapter<CategoryChipAdapter.ChipViewHolder> {
    private List<Category> categories = new ArrayList<>();
    private int selectedPosition = 0;
    private OnCategorySelectedListener listener;

    public interface OnCategorySelectedListener {
        void onCategorySelected(Long categoryId);
    }

    public CategoryChipAdapter(OnCategorySelectedListener listener) {
        this.listener = listener;
        Category allCategory = new Category();
        allCategory.setId(null);
        allCategory.setName("Tất cả");
        categories.add(allCategory);
    }

    public void setCategories(List<Category> newCategories) {
        Category allCategory = categories.get(0);
        categories.clear();
        categories.add(allCategory);
        if (newCategories != null) {
            categories.addAll(newCategories);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChipViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Chip chip = (Chip) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category_chip, parent, false);
        return new ChipViewHolder(chip);
    }

    @Override
    public void onBindViewHolder(@NonNull ChipViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.bind(category, position == selectedPosition);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    class ChipViewHolder extends RecyclerView.ViewHolder {
        private final Chip chip;

        ChipViewHolder(Chip chip) {
            super(chip);
            this.chip = chip;
        }

        void bind(Category category, boolean isSelected) {
            chip.setText(category.getName());
            chip.setChecked(isSelected);

            chip.setOnClickListener(v -> {
                int oldPosition = selectedPosition;
                selectedPosition = getAdapterPosition();
                notifyItemChanged(oldPosition);
                notifyItemChanged(selectedPosition);

                if (listener != null) {
                    listener.onCategorySelected(category.getId());
                }
            });
        }
    }
}
