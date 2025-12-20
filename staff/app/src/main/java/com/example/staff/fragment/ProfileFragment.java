package com.example.staff.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.staff.R;
import com.example.staff.activity.LoginActivity;
import com.example.staff.api.RetrofitClient;
import com.example.staff.databinding.FragmentProfileBinding;
import com.example.staff.util.CartManager;

public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadUserInfo();
        setupListeners();
    }

    private void loadUserInfo() {
        String fullName = RetrofitClient.getUserFullName(requireContext());
        String email = RetrofitClient.getUserEmail(requireContext());
        String role = RetrofitClient.getUserRole(requireContext());

        if (fullName != null && !fullName.isEmpty()) {
            binding.tvFullName.setText(fullName);
            binding.tvAvatarInitial.setText(String.valueOf(fullName.charAt(0)).toUpperCase());
        } else {
            binding.tvFullName.setText("Nhân viên");
            binding.tvAvatarInitial.setText("N");
        }

        binding.tvEmail.setText(email != null ? email : "");
        binding.tvRole.setText(role != null ? role : "Staff");
    }

    private void setupListeners() {
        binding.btnLogout.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle(R.string.logout)
                    .setMessage(R.string.logout_confirm)
                    .setPositiveButton(R.string.yes, (dialog, which) -> logout())
                    .setNegativeButton(R.string.no, null)
                    .show();
        });
    }

    private void logout() {
        CartManager.getInstance().clearCart();

        RetrofitClient.clearToken(requireContext());

        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
