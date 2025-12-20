package com.example.foodbe.controllers;

import com.example.foodbe.models.UserPending;
import com.example.foodbe.payload.ApiResponse;
import com.example.foodbe.request.user.UserCreateDTO;
import com.example.foodbe.services.UserPendingService;
import com.example.foodbe.utils.ConstantUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/userPending")
@RequiredArgsConstructor
@Validated
public class UserPendingController {

    private final UserPendingService userPendingService;

    @GetMapping("")
    public ResponseEntity<ApiResponse<List<UserPending>>> getAll(){
        return ResponseEntity.ok(ApiResponse.success(userPendingService.findAll()));
    }

    @PostMapping("")
    public ResponseEntity<ApiResponse<String>> create(@Valid @RequestBody UserCreateDTO userCreateDTO){
        userPendingService.create(userCreateDTO);
        return ResponseEntity.ok(ApiResponse.success(ConstantUtils.CREATE_SUCCESSFULLY));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable Long id){
        userPendingService.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success(ConstantUtils.DELETE_SUCCESSFULLY));
    }
}
