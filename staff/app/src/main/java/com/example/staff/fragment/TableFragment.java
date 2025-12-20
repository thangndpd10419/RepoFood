package com.example.staff.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.staff.R;
import com.example.staff.activity.MainActivity;
import com.example.staff.adapter.TableAdapter;
import com.example.staff.databinding.FragmentTableBinding;
import com.example.staff.util.CartManager;

public class TableFragment extends Fragment implements TableAdapter.OnTableClickListener {
    private FragmentTableBinding binding;
    private TableAdapter tableAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentTableBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView();
        setupListeners();
        updateSelectedTableUI();
    }

    private void setupRecyclerView() {
        tableAdapter = new TableAdapter(this);
        binding.recyclerViewTables.setLayoutManager(new GridLayoutManager(requireContext(), 4));
        binding.recyclerViewTables.setAdapter(tableAdapter);

        Integer selectedTable = CartManager.getInstance().getSelectedTable();
        if (selectedTable != null) {
            tableAdapter.setSelectedTable(selectedTable);
        }
    }

    private void setupListeners() {
        binding.btnContinue.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).navigateToProducts();
            }
        });
    }

    private void updateSelectedTableUI() {
        Integer selectedTable = CartManager.getInstance().getSelectedTable();
        if (selectedTable != null && selectedTable > 0) {
            binding.tvSelectedTable.setText(String.format("Đã chọn: Bàn %d", selectedTable));
            binding.btnContinue.setEnabled(true);
        } else {
            binding.tvSelectedTable.setText("Chưa chọn bàn");
            binding.btnContinue.setEnabled(false);
        }
    }

    @Override
    public void onTableClick(int tableNumber) {
        CartManager.getInstance().setSelectedTable(tableNumber);
        tableAdapter.setSelectedTable(tableNumber);
        updateSelectedTableUI();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
