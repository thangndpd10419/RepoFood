package com.example.admin.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.admin.adapter.CategoryAdapter;
import com.example.admin.api.RetrofitClient;
import com.example.admin.databinding.DialogCategoryBinding;
import com.example.admin.databinding.FragmentCategoryBinding;
import com.example.admin.model.ApiResponse;
import com.example.admin.model.Category;
import com.example.admin.model.PageResponse;
import com.example.admin.util.ImageUploadHelper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryFragment extends Fragment implements CategoryAdapter.OnCategoryClickListener {
    private FragmentCategoryBinding binding;
    private CategoryAdapter adapter;

    private DialogCategoryBinding currentDialogBinding;
    private Uri selectedImageUri;
    private String currentImageUrl;
    private AlertDialog currentDialog;

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    if (selectedImageUri != null && currentDialogBinding != null) {
                        Glide.with(this)
                                .load(selectedImageUri)
                                .centerCrop()
                                .into(currentDialogBinding.ivPreview);
                        currentDialogBinding.layoutPickImage.setVisibility(View.GONE);
                    }
                }
            }
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentCategoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView();
        setupSwipeRefresh();
        setupFab();
        loadCategories();
    }

    private void setupRecyclerView() {
        adapter = new CategoryAdapter();
        adapter.setOnCategoryClickListener(this);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerView.setAdapter(adapter);
    }

    private void setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener(this::loadCategories);
    }

    private void setupFab() {
        binding.fabAdd.setOnClickListener(v -> showAddDialog());
    }

    private void loadCategories() {
        showLoading(true);

        RetrofitClient.getApiService().getCategories(0, 100).enqueue(new Callback<ApiResponse<PageResponse<Category>>>() {
            @Override
            public void onResponse(Call<ApiResponse<PageResponse<Category>>> call,
                                   Response<ApiResponse<PageResponse<Category>>> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<PageResponse<Category>> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        PageResponse<Category> pageData = apiResponse.getData();
                        adapter.setCategories(pageData.getContent());
                        updateEmptyState(pageData.getContent().isEmpty());
                        long count = pageData.getTotalElements() > 0 ? pageData.getTotalElements() : pageData.getContent().size();
                        binding.tvCategoryCount.setText(count + " danh mục");
                    } else {
                        showError(apiResponse.getMessage());
                    }
                } else {
                    showError("Lỗi: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<PageResponse<Category>>> call, Throwable t) {
                showLoading(false);
                showError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    private void showAddDialog() {
        currentDialogBinding = DialogCategoryBinding.inflate(getLayoutInflater());
        selectedImageUri = null;
        currentImageUrl = null;

        setupImagePicker(currentDialogBinding, null);

        currentDialog = new AlertDialog.Builder(requireContext())
                .setTitle("Thêm danh mục mới")
                .setView(currentDialogBinding.getRoot())
                .setPositiveButton("Thêm", null)
                .setNegativeButton("Hủy", null)
                .create();

        currentDialog.setOnShowListener(dialog -> {
            currentDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                String name = currentDialogBinding.etName.getText().toString().trim();

                if (name.isEmpty()) {
                    Toast.makeText(requireContext(), "Vui lòng nhập tên danh mục", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (selectedImageUri != null) {
                    uploadAndCreateCategory(name);
                } else {
                    createCategory(name, currentImageUrl);
                }
            });
        });

        currentDialog.show();
    }

    @Override
    public void onEditClick(Category category) {
        currentDialogBinding = DialogCategoryBinding.inflate(getLayoutInflater());
        currentDialogBinding.etName.setText(category.getName());
        selectedImageUri = null;
        currentImageUrl = category.getImgCategory();

        setupImagePicker(currentDialogBinding, category.getImgCategory());

        currentDialog = new AlertDialog.Builder(requireContext())
                .setTitle("Sửa danh mục")
                .setView(currentDialogBinding.getRoot())
                .setPositiveButton("Lưu", null)
                .setNegativeButton("Hủy", null)
                .create();

        currentDialog.setOnShowListener(dialog -> {
            currentDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                String name = currentDialogBinding.etName.getText().toString().trim();

                if (name.isEmpty()) {
                    Toast.makeText(requireContext(), "Vui lòng nhập tên danh mục", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (selectedImageUri != null) {
                    uploadAndUpdateCategory(category.getId(), name);
                } else {
                    updateCategory(category.getId(), name, currentImageUrl);
                }
            });
        });

        currentDialog.show();
    }

    private void setupImagePicker(DialogCategoryBinding dialogBinding, String existingImageUrl) {
        if (existingImageUrl != null && !existingImageUrl.isEmpty()) {
            String fullUrl = existingImageUrl;
            if (!existingImageUrl.startsWith("http://") && !existingImageUrl.startsWith("https://")) {
                fullUrl = "http://10.0.2.2:8080/" + existingImageUrl;
            }
            Glide.with(this)
                    .load(fullUrl)
                    .centerCrop()
                    .into(dialogBinding.ivPreview);
            dialogBinding.layoutPickImage.setVisibility(View.GONE);
        }

        dialogBinding.layoutPickImage.setOnClickListener(v -> openImagePicker());
        dialogBinding.ivPreview.setOnClickListener(v -> openImagePicker());
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private void uploadAndCreateCategory(String name) {
        if (currentDialogBinding != null) {
            currentDialogBinding.progressUpload.setVisibility(View.VISIBLE);
        }

        ImageUploadHelper.uploadImage(requireContext(), selectedImageUri, "categories",
                currentDialogBinding != null ? currentDialogBinding.progressUpload : null,
                new ImageUploadHelper.UploadCallback() {
                    @Override
                    public void onSuccess(String imageUrl) {
                        createCategory(name, imageUrl);
                    }

                    @Override
                    public void onError(String message) {
                        showError(message);
                    }
                });
    }

    private void uploadAndUpdateCategory(Long id, String name) {
        if (currentDialogBinding != null) {
            currentDialogBinding.progressUpload.setVisibility(View.VISIBLE);
        }

        ImageUploadHelper.uploadImage(requireContext(), selectedImageUri, "categories",
                currentDialogBinding != null ? currentDialogBinding.progressUpload : null,
                new ImageUploadHelper.UploadCallback() {
                    @Override
                    public void onSuccess(String imageUrl) {
                        updateCategory(id, name, imageUrl);
                    }

                    @Override
                    public void onError(String message) {
                        showError(message);
                    }
                });
    }

    @Override
    public void onDeleteClick(Category category) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xóa danh mục")
                .setMessage("Bạn có chắc chắn muốn xóa \"" + category.getName() + "\"?")
                .setPositiveButton("Xóa", (dialog, which) -> deleteCategory(category))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void createCategory(String name, String image) {
        showLoading(true);
        if (currentDialog != null) currentDialog.dismiss();

        Long userId = RetrofitClient.getUserId(requireContext());
        Category category = new Category(name, image, userId);

        RetrofitClient.getApiService().createCategory(category).enqueue(new Callback<ApiResponse<Category>>() {
            @Override
            public void onResponse(Call<ApiResponse<Category>> call, Response<ApiResponse<Category>> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(requireContext(), "Đã thêm danh mục", Toast.LENGTH_SHORT).show();
                    loadCategories();
                } else {
                    showError("Không thể thêm danh mục");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Category>> call, Throwable t) {
                showLoading(false);
                showError("Lỗi: " + t.getMessage());
            }
        });
    }

    private void updateCategory(Long id, String name, String image) {
        showLoading(true);
        if (currentDialog != null) currentDialog.dismiss();

        Long userId = RetrofitClient.getUserId(requireContext());
        Category category = new Category(name, image, userId);

        RetrofitClient.getApiService().updateCategory(id, category).enqueue(new Callback<ApiResponse<Category>>() {
            @Override
            public void onResponse(Call<ApiResponse<Category>> call, Response<ApiResponse<Category>> response) {
                showLoading(false);

                if (response.isSuccessful()) {
                    Toast.makeText(requireContext(), "Đã cập nhật danh mục", Toast.LENGTH_SHORT).show();
                    loadCategories();
                } else {
                    showError("Không thể cập nhật danh mục");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Category>> call, Throwable t) {
                showLoading(false);
                showError("Lỗi: " + t.getMessage());
            }
        });
    }

    private void deleteCategory(Category category) {
        showLoading(true);

        RetrofitClient.getApiService().deleteCategory(category.getId()).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                showLoading(false);

                if (response.isSuccessful()) {
                    Toast.makeText(requireContext(), "Đã xóa danh mục", Toast.LENGTH_SHORT).show();
                    loadCategories();
                } else {
                    showError("Không thể xóa danh mục");
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
