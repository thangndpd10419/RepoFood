package com.example.staff.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.staff.R;

public class TableAdapter extends RecyclerView.Adapter<TableAdapter.TableViewHolder> {
    private int tableCount = 20;
    private int selectedTable = -1;
    private OnTableClickListener listener;

    public interface OnTableClickListener {
        void onTableClick(int tableNumber);
    }

    public TableAdapter(OnTableClickListener listener) {
        this.listener = listener;
    }

    public void setSelectedTable(int tableNumber) {
        int oldSelected = selectedTable;
        selectedTable = tableNumber;
        if (oldSelected > 0) {
            notifyItemChanged(oldSelected - 1);
        }
        if (selectedTable > 0) {
            notifyItemChanged(selectedTable - 1);
        }
    }

    public int getSelectedTable() {
        return selectedTable;
    }

    @NonNull
    @Override
    public TableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_table, parent, false);
        return new TableViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TableViewHolder holder, int position) {
        int tableNumber = position + 1;
        holder.bind(tableNumber, tableNumber == selectedTable);
    }

    @Override
    public int getItemCount() {
        return tableCount;
    }

    class TableViewHolder extends RecyclerView.ViewHolder {
        private final CardView cardView;
        private final LinearLayout tableContainer;
        private final TextView tvTableNumber;
        private final TextView tvTableStatus;
        private final View statusIndicator;

        TableViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView;
            tableContainer = itemView.findViewById(R.id.tableContainer);
            tvTableNumber = itemView.findViewById(R.id.tvTableNumber);
            tvTableStatus = itemView.findViewById(R.id.tvTableStatus);
            statusIndicator = itemView.findViewById(R.id.statusIndicator);
        }

        void bind(int tableNumber, boolean isSelected) {
            tvTableNumber.setText(String.valueOf(tableNumber));

            if (isSelected) {
                cardView.setCardBackgroundColor(Color.parseColor("#FFF7ED"));
                tvTableNumber.setTextColor(Color.parseColor("#f97316"));
                tvTableStatus.setText("Đã chọn");
                tvTableStatus.setTextColor(Color.parseColor("#f97316"));
                statusIndicator.setBackgroundColor(Color.parseColor("#f97316"));
            } else {
                cardView.setCardBackgroundColor(Color.WHITE);
                tvTableNumber.setTextColor(Color.parseColor("#1F2937"));
                tvTableStatus.setText("Trống");
                tvTableStatus.setTextColor(Color.parseColor("#6B7280"));
                statusIndicator.setBackgroundColor(Color.parseColor("#10B981"));
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onTableClick(tableNumber);
                }
            });
        }
    }
}
