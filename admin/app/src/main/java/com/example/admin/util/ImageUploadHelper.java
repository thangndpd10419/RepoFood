package com.example.admin.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.admin.api.RetrofitClient;
import com.example.admin.model.ApiResponse;
import com.example.admin.model.UploadResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ImageUploadHelper {

    public interface UploadCallback {
        void onSuccess(String imageUrl);
        void onError(String message);
    }

    public static void uploadImage(Context context, Uri imageUri, String folder,
                                   ProgressBar progressBar, UploadCallback callback) {
        if (imageUri == null) {
            callback.onError("Không có ảnh để upload");
            return;
        }

        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }

        try {
            File file = getFileFromUri(context, imageUri);
            if (file == null) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                callback.onError("Không thể đọc file ảnh");
                return;
            }

            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
            RequestBody folderBody = RequestBody.create(MediaType.parse("text/plain"), folder);

            RetrofitClient.getApiService().uploadImage(body, folderBody).enqueue(new Callback<ApiResponse<UploadResponse>>() {
                @Override
                public void onResponse(Call<ApiResponse<UploadResponse>> call, Response<ApiResponse<UploadResponse>> response) {
                    if (progressBar != null) progressBar.setVisibility(View.GONE);

                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        UploadResponse data = response.body().getData();
                        if (data != null && data.getUrl() != null) {
                            callback.onSuccess(data.getUrl());
                        } else {
                            callback.onError("Upload thất bại");
                        }
                    } else {
                        callback.onError("Upload thất bại: " + response.code());
                    }

                    if (file.exists()) {
                        file.delete();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<UploadResponse>> call, Throwable t) {
                    if (progressBar != null) progressBar.setVisibility(View.GONE);
                    callback.onError("Lỗi kết nối: " + t.getMessage());

                    if (file.exists()) {
                        file.delete();
                    }
                }
            });

        } catch (Exception e) {
            if (progressBar != null) progressBar.setVisibility(View.GONE);
            callback.onError("Lỗi: " + e.getMessage());
        }
    }

    private static File getFileFromUri(Context context, Uri uri) {
        try {
            String fileName = getFileName(context, uri);
            File tempFile = new File(context.getCacheDir(), fileName);

            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            FileOutputStream outputStream = new FileOutputStream(tempFile);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.close();
            inputStream.close();

            return tempFile;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String getFileName(Context context, Uri uri) {
        String result = "temp_image.jpg";
        if (uri.getScheme().equals("content")) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (index >= 0) {
                        result = cursor.getString(index);
                    }
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        return result;
    }

    public static void loadImage(Context context, String url, ImageView imageView, View placeholder) {
        if (url != null && !url.isEmpty()) {
            if (placeholder != null) placeholder.setVisibility(View.GONE);
            Glide.with(context)
                    .load(url)
                    .centerCrop()
                    .into(imageView);
        } else {
            if (placeholder != null) placeholder.setVisibility(View.VISIBLE);
            imageView.setImageResource(0);
        }
    }
}
