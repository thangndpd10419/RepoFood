package com.example.foodbe.controllers;

import com.example.foodbe.payload.ApiResponse;
import com.example.foodbe.services.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
public class UploadController {


    private final CloudinaryService cloudinaryService;

    @PostMapping("/image")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "folder", defaultValue = "foodbe") String folder) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.error(400, null, "File không được để trống")
            );
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.error(400, null, "Chỉ chấp nhận file ảnh")
            );
        }

        if (file.getSize() > 5 * 1024 * 1024) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.error(400, null, "Kích thước file không được vượt quá 5MB")
            );
        }

        Map<String, Object> result = cloudinaryService.uploadImage(file, folder);

        Map<String, Object> response = new HashMap<>();
        response.put("url", result.get("secure_url"));
        response.put("publicId", result.get("public_id"));
        response.put("width", result.get("width"));
        response.put("height", result.get("height"));

        return ResponseEntity.ok(ApiResponse.success(response, "Upload ảnh thành công"));
    }

    @DeleteMapping("/image")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<String>> deleteImage(@RequestParam("publicId") String publicId) {
        cloudinaryService.deleteImage(publicId);
        return ResponseEntity.ok(ApiResponse.success("Deleted", "Xóa ảnh thành công"));
    }
}
