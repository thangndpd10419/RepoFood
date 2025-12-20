package com.example.admin.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.bumptech.glide.Glide;
import com.example.admin.adapter.ProductAdapter;
import com.example.admin.api.RetrofitClient;
import com.example.admin.databinding.DialogProductBinding;
import com.example.admin.databinding.FragmentProductBinding;
import com.example.admin.model.ApiResponse;
import com.example.admin.model.Category;
import com.example.admin.model.PageResponse;
import com.example.admin.model.Product;
import com.example.admin.util.ImageUploadHelper;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductFragment extends Fragment implements ProductAdapter.OnProductClickListener {
    private FragmentProductBinding binding;
    private ProductAdapter adapter;
    private List<Category> categories = new ArrayList<>();

    private DialogProductBinding currentDialogBinding;
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
        binding = FragmentProductBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView();
        setupSwipeRefresh();
        setupSearch();
        setupFab();
        loadCategories();
        loadProducts(null);
    }

    private void setupRecyclerView() {
        adapter = new ProductAdapter();
        adapter.setOnProductClickListener(this);
        binding.recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        binding.recyclerView.setAdapter(adapter);
    }

    private void setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener(() -> loadProducts(null));
    }

    private void setupSearch() {
        binding.etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String query = binding.etSearch.getText().toString().trim();
                loadProducts(query.isEmpty() ? null : query);
                return true;
            }
            return false;
        });
    }

    private void setupFab() {
        binding.fabAdd.setOnClickListener(v -> showAddDialog());
    }

    private void loadCategories() {
        RetrofitClient.getApiService().getCategories(0, 100).enqueue(new Callback<ApiResponse<PageResponse<Category>>>() {
            @Override
            public void onResponse(Call<ApiResponse<PageResponse<Category>>> call,
                                   Response<ApiResponse<PageResponse<Category>>> response) {
                if (response.isSuccessful() && response.body() != null &&
                        response.body().isSuccess() && response.body().getData() != null) {
                    categories = response.body().getData().getContent();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<PageResponse<Category>>> call, Throwable t) {
            }
        });
    }

    private void loadProducts(String search) {
        showLoading(true);

        RetrofitClient.getApiService().getProducts(0, 100, search).enqueue(new Callback<ApiResponse<PageResponse<Product>>>() {
            @Override
            public void onResponse(Call<ApiResponse<PageResponse<Product>>> call,
                                   Response<ApiResponse<PageResponse<Product>>> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<PageResponse<Product>> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        PageResponse<Product> pageData = apiResponse.getData();
                        adapter.setProducts(pageData.getContent());
                        updateEmptyState(pageData.getContent().isEmpty());
                        long count = pageData.getTotalElements() > 0 ? pageData.getTotalElements() : pageData.getContent().size();
                        binding.tvProductCount.setText(count + " sản phẩm");
                    } else {
                        showError(apiResponse.getMessage());
                    }
                } else {
                    showError("Lỗi: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<PageResponse<Product>>> call, Throwable t) {
                showLoading(false);
                showError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    private void showAddDialog() {
        if (categories.isEmpty()) {
            Toast.makeText(requireContext(), "Vui lòng tạo danh mục trước", Toast.LENGTH_SHORT).show();
            return;
        }

        currentDialogBinding = DialogProductBinding.inflate(getLayoutInflater());
        selectedImageUri = null;
        currentImageUrl = null;

        setupCategorySpinner(currentDialogBinding, null);
        setupImagePicker(currentDialogBinding, null);

        currentDialog = new AlertDialog.Builder(requireContext())
                .setTitle("Thêm sản phẩm mới")
                .setView(currentDialogBinding.getRoot())
                .setPositiveButton("Thêm", null)
                .setNegativeButton("Hủy", null)
                .create();

        currentDialog.setOnShowListener(dialog -> {
            currentDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                if (validateInput()) {
                    if (selectedImageUri != null) {
                        uploadAndCreateProduct();
                    } else {
                        createProductFromDialog(currentImageUrl);
                    }
                }
            });
        });

        currentDialog.show();
    }

    @Override
    public void onEditClick(Product product) {
        if (categories.isEmpty()) {
            Toast.makeText(requireContext(), "Đang tải danh mục...", Toast.LENGTH_SHORT).show();
            return;
        }

        currentDialogBinding = DialogProductBinding.inflate(getLayoutInflater());
        currentDialogBinding.etName.setText(product.getName());
        currentDialogBinding.etPrice.setText(String.valueOf(product.getPrice() != null ? product.getPrice().intValue() : 0));
        currentDialogBinding.etQuantity.setText(String.valueOf(product.getQuantity() != null ? product.getQuantity() : 0));
        currentDialogBinding.etDescription.setText(product.getDescription());
        selectedImageUri = null;
        currentImageUrl = product.getImgProduct();

        setupCategorySpinner(currentDialogBinding, product.getCategoryId());
        setupImagePicker(currentDialogBinding, product.getImgProduct());

        currentDialog = new AlertDialog.Builder(requireContext())
                .setTitle("Sửa sản phẩm")
                .setView(currentDialogBinding.getRoot())
                .setPositiveButton("Lưu", null)
                .setNegativeButton("Hủy", null)
                .create();

        currentDialog.setOnShowListener(dialog -> {
            currentDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                if (validateInput()) {
                    if (selectedImageUri != null) {
                        uploadAndUpdateProduct(product.getId());
                    } else {
                        updateProductFromDialog(product.getId(), currentImageUrl);
                    }
                }
            });
        });

        currentDialog.show();
    }

    private void setupImagePicker(DialogProductBinding dialogBinding, String existingImageUrl) {
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

    private void setupCategorySpinner(DialogProductBinding dialogBinding, Long selectedCategoryId) {
        List<String> categoryNames = new ArrayList<>();
        int selectedPosition = 0;

        for (int i = 0; i < categories.size(); i++) {
            Category cat = categories.get(i);
            categoryNames.add(cat.getName());
            if (selectedCategoryId != null && cat.getId().equals(selectedCategoryId)) {
                selectedPosition = i;
            }
        }

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                categoryNames
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dialogBinding.spinnerCategory.setAdapter(spinnerAdapter);
        dialogBinding.spinnerCategory.setSelection(selectedPosition);
    }

    private boolean validateInput() {
        String name = currentDialogBinding.etName.getText().toString().trim();
        String priceStr = currentDialogBinding.etPrice.getText().toString().trim();
        String quantityStr = currentDialogBinding.etQuantity.getText().toString().trim();

        if (name.isEmpty()) {
            Toast.makeText(requireContext(), "Vui lòng nhập tên sản phẩm", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (priceStr.isEmpty()) {
            Toast.makeText(requireContext(), "Vui lòng nhập giá sản phẩm", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (quantityStr.isEmpty()) {
            Toast.makeText(requireContext(), "Vui lòng nhập số lượng", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void uploadAndCreateProduct() {
        currentDialogBinding.progressUpload.setVisibility(View.VISIBLE);

        ImageUploadHelper.uploadImage(requireContext(), selectedImageUri, "products",
                currentDialogBinding.progressUpload,
                new ImageUploadHelper.UploadCallback() {
                    @Override
                    public void onSuccess(String imageUrl) {
                        createProductFromDialog(imageUrl);
                    }

                    @Override
                    public void onError(String message) {
                        showError(message);
                    }
                });
    }

    private void uploadAndUpdateProduct(Long productId) {
        currentDialogBinding.progressUpload.setVisibility(View.VISIBLE);

        ImageUploadHelper.uploadImage(requireContext(), selectedImageUri, "products",
                currentDialogBinding.progressUpload,
                new ImageUploadHelper.UploadCallback() {
                    @Override
                    public void onSuccess(String imageUrl) {
                        updateProductFromDialog(productId, imageUrl);
                    }

                    @Override
                    public void onError(String message) {
                        showError(message);
                    }
                });
    }

    private void createProductFromDialog(String imageUrl) {
        String name = currentDialogBinding.etName.getText().toString().trim();
        String priceStr = currentDialogBinding.etPrice.getText().toString().trim();
        String quantityStr = currentDialogBinding.etQuantity.getText().toString().trim();
        String description = currentDialogBinding.etDescription.getText().toString().trim();
        int categoryPos = currentDialogBinding.spinnerCategory.getSelectedItemPosition();

        createProduct(name, Double.parseDouble(priceStr), Integer.parseInt(quantityStr),
                imageUrl, description, categories.get(categoryPos).getId());
    }

    private void updateProductFromDialog(Long productId, String imageUrl) {
        String name = currentDialogBinding.etName.getText().toString().trim();
        String priceStr = currentDialogBinding.etPrice.getText().toString().trim();
        String quantityStr = currentDialogBinding.etQuantity.getText().toString().trim();
        String description = currentDialogBinding.etDescription.getText().toString().trim();
        int categoryPos = currentDialogBinding.spinnerCategory.getSelectedItemPosition();

        updateProduct(productId, name, Double.parseDouble(priceStr), Integer.parseInt(quantityStr),
                imageUrl, description, categories.get(categoryPos).getId());
    }

    @Override
    public void onDeleteClick(Product product) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xóa sản phẩm")
                .setMessage("Bạn có chắc chắn muốn xóa \"" + product.getName() + "\"?")
                .setPositiveButton("Xóa", (dialog, which) -> deleteProduct(product))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void createProduct(String name, Double price, Integer quantity,
                               String image, String description, Long categoryId) {
        showLoading(true);
        if (currentDialog != null) currentDialog.dismiss();

        Product product = new Product(name, quantity, price, image, description, categoryId);

        RetrofitClient.getApiService().createProduct(product).enqueue(new Callback<ApiResponse<Product>>() {
            @Override
            public void onResponse(Call<ApiResponse<Product>> call, Response<ApiResponse<Product>> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(requireContext(), "Đã thêm sản phẩm", Toast.LENGTH_SHORT).show();
                    loadProducts(null);
                } else {
                    showError("Không thể thêm sản phẩm");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Product>> call, Throwable t) {
                showLoading(false);
                showError("Lỗi: " + t.getMessage());
            }
        });
    }

    private void updateProduct(Long id, String name, Double price, Integer quantity,
                               String image, String description, Long categoryId) {
        showLoading(true);
        if (currentDialog != null) currentDialog.dismiss();

        Product product = new Product(name, quantity, price, image, description, categoryId);

        RetrofitClient.getApiService().updateProduct(id, product).enqueue(new Callback<ApiResponse<Product>>() {
            @Override
            public void onResponse(Call<ApiResponse<Product>> call, Response<ApiResponse<Product>> response) {
                showLoading(false);

                if (response.isSuccessful()) {
                    Toast.makeText(requireContext(), "Đã cập nhật sản phẩm", Toast.LENGTH_SHORT).show();
                    loadProducts(null);
                } else {
                    showError("Không thể cập nhật sản phẩm");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Product>> call, Throwable t) {
                showLoading(false);
                showError("Lỗi: " + t.getMessage());
            }
        });
    }

    private void deleteProduct(Product product) {
        showLoading(true);

        RetrofitClient.getApiService().deleteProduct(product.getId()).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                showLoading(false);

                if (response.isSuccessful()) {
                    Toast.makeText(requireContext(), "Đã xóa sản phẩm", Toast.LENGTH_SHORT).show();
                    loadProducts(null);
                } else {
                    showError("Không thể xóa sản phẩm");
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
