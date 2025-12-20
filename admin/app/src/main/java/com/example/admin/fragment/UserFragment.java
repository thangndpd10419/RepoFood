package com.example.admin.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.admin.adapter.UserAdapter;
import com.example.admin.api.RetrofitClient;
import com.example.admin.databinding.DialogUserBinding;
import com.example.admin.databinding.FragmentUserBinding;
import com.example.admin.model.ApiResponse;
import com.example.admin.model.PageResponse;
import com.example.admin.model.User;
import com.example.admin.model.UserCreateRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserFragment extends Fragment implements UserAdapter.OnUserClickListener {
    private FragmentUserBinding binding;
    private UserAdapter adapter;

    private static final String[] ROLES = {"ADMIN", "STAFF"};
    private static final String[] STATUSES = {"ACTIVE", "INACTIVE"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentUserBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView();
        setupSwipeRefresh();
        setupFab();
        loadUsers();
    }

    private void setupRecyclerView() {
        adapter = new UserAdapter();
        adapter.setOnUserClickListener(this);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerView.setAdapter(adapter);
    }

    private void setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener(this::loadUsers);
    }

    private void setupFab() {
        binding.fabAdd.setOnClickListener(v -> showAddDialog());
    }

    private void loadUsers() {
        showLoading(true);

        RetrofitClient.getApiService().getUsers(0, 100).enqueue(new Callback<ApiResponse<PageResponse<User>>>() {
            @Override
            public void onResponse(Call<ApiResponse<PageResponse<User>>> call,
                                   Response<ApiResponse<PageResponse<User>>> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<PageResponse<User>> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        PageResponse<User> pageData = apiResponse.getData();
                        adapter.setUsers(pageData.getContent());
                        updateEmptyState(pageData.getContent().isEmpty());
                        long count = pageData.getTotalElements() > 0 ? pageData.getTotalElements() : pageData.getContent().size();
                        binding.tvUserCount.setText(count + " người dùng");
                    } else {
                        showError(apiResponse.getMessage());
                    }
                } else {
                    showError("Lỗi: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<PageResponse<User>>> call, Throwable t) {
                showLoading(false);
                showError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    private void showAddDialog() {
        DialogUserBinding dialogBinding = DialogUserBinding.inflate(getLayoutInflater());

        setupSpinners(dialogBinding);

        dialogBinding.layoutPassword.setVisibility(View.VISIBLE);

        new AlertDialog.Builder(requireContext())
                .setTitle("Thêm người dùng mới")
                .setView(dialogBinding.getRoot())
                .setPositiveButton("Thêm", (dialog, which) -> {
                    String name = dialogBinding.etName.getText().toString().trim();
                    String email = dialogBinding.etEmail.getText().toString().trim();
                    String password = dialogBinding.etPassword.getText().toString().trim();
                    String phone = dialogBinding.etPhone.getText().toString().trim();
                    String role = ROLES[dialogBinding.spinnerRole.getSelectedItemPosition()];
                    String status = STATUSES[dialogBinding.spinnerStatus.getSelectedItemPosition()];

                    if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                        Toast.makeText(requireContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\W).{6,}$";
                    if (!password.matches(passwordPattern)) {
                        Toast.makeText(requireContext(), "Mật khẩu phải có chữ hoa, chữ thường và ký tự đặc biệt", Toast.LENGTH_LONG).show();
                        return;
                    }

                    createUser(name, email, password, phone, role, status);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    public void onEditClick(User user) {
        DialogUserBinding dialogBinding = DialogUserBinding.inflate(getLayoutInflater());

        dialogBinding.etName.setText(user.getFullName());
        dialogBinding.etEmail.setText(user.getEmail());
        dialogBinding.etPhone.setText(user.getPhone());

        setupSpinners(dialogBinding);

        for (int i = 0; i < ROLES.length; i++) {
            if (ROLES[i].equalsIgnoreCase(user.getRole())) {
                dialogBinding.spinnerRole.setSelection(i);
                break;
            }
        }

        for (int i = 0; i < STATUSES.length; i++) {
            if (STATUSES[i].equalsIgnoreCase(user.getStatus())) {
                dialogBinding.spinnerStatus.setSelection(i);
                break;
            }
        }

        dialogBinding.layoutPassword.setVisibility(View.GONE);

        new AlertDialog.Builder(requireContext())
                .setTitle("Sửa người dùng")
                .setView(dialogBinding.getRoot())
                .setPositiveButton("Lưu", (dialog, which) -> {
                    String name = dialogBinding.etName.getText().toString().trim();
                    String email = dialogBinding.etEmail.getText().toString().trim();
                    String phone = dialogBinding.etPhone.getText().toString().trim();
                    String role = ROLES[dialogBinding.spinnerRole.getSelectedItemPosition()];
                    String status = STATUSES[dialogBinding.spinnerStatus.getSelectedItemPosition()];

                    if (name.isEmpty() || email.isEmpty()) {
                        Toast.makeText(requireContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    updateUser(user.getId(), name, email, phone, role, status);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void setupSpinners(DialogUserBinding dialogBinding) {
        ArrayAdapter<String> roleAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                new String[]{"Quản trị viên (ADMIN)", "Nhân viên (STAFF)"}
        );
        roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dialogBinding.spinnerRole.setAdapter(roleAdapter);

        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                new String[]{"Đang hoạt động", "Ngừng hoạt động"}
        );
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dialogBinding.spinnerStatus.setAdapter(statusAdapter);
    }

    @Override
    public void onDeleteClick(User user) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xóa người dùng")
                .setMessage("Bạn có chắc chắn muốn xóa " + user.getEmail() + "?")
                .setPositiveButton("Xóa", (dialog, which) -> deleteUser(user))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void createUser(String name, String email, String password, String phone, String role, String status) {
        showLoading(true);

        UserCreateRequest request = new UserCreateRequest(name, email, password, phone, role, status);

        RetrofitClient.getApiService().createUser(request).enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(requireContext(), "Đã thêm người dùng", Toast.LENGTH_SHORT).show();
                    loadUsers();
                } else {
                    String errorMsg = "Không thể thêm người dùng";
                    if (response.body() != null && response.body().getMessage() != null) {
                        errorMsg = response.body().getMessage();
                    }
                    showError(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                showLoading(false);
                showError("Lỗi: " + t.getMessage());
            }
        });
    }

    private void updateUser(Long id, String name, String email, String phone, String role, String status) {
        showLoading(true);

        User user = new User();
        user.setId(id);
        user.setFullName(name);
        user.setEmail(email);
        user.setPhone(phone);
        user.setRole(role);
        user.setStatus(status);

        RetrofitClient.getApiService().updateUser(id, user).enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                showLoading(false);

                if (response.isSuccessful()) {
                    Toast.makeText(requireContext(), "Đã cập nhật người dùng", Toast.LENGTH_SHORT).show();
                    loadUsers();
                } else {
                    showError("Không thể cập nhật người dùng");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                showLoading(false);
                showError("Lỗi: " + t.getMessage());
            }
        });
    }

    private void deleteUser(User user) {
        showLoading(true);

        RetrofitClient.getApiService().deleteUser(user.getId()).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                showLoading(false);

                if (response.isSuccessful()) {
                    Toast.makeText(requireContext(), "Đã xóa người dùng", Toast.LENGTH_SHORT).show();
                    loadUsers();
                } else {
                    showError("Không thể xóa người dùng");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                showLoading(false);
                showError("Lỗi: " + t.getMessage());
            }
        });
    }

    private void showLoading(boolean show) {
        binding.swipeRefresh.setRefreshing(false);
        binding.progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void updateEmptyState(boolean empty) {
        binding.tvEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
        binding.recyclerView.setVisibility(empty ? View.GONE : View.VISIBLE);
    }

    private void showError(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
