package com.example.foodbe.controllers;

import com.example.foodbe.payload.ApiResponse;
import com.example.foodbe.request.cart.CreateCartDTO;
import com.example.foodbe.request.cart.UpdateCartDTO;
import com.example.foodbe.response.cart.CartResponseDTO;
import com.example.foodbe.services.CartService;
import com.example.foodbe.utils.ConstantUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
@Validated
public class CartController {
    private final CartService cartService;


    // vi cart it nên có thể trả về list, khong trả page

    @PreAuthorize("hasRole('STAFF')")
    @GetMapping("")
    public ResponseEntity<ApiResponse<List<CartResponseDTO>>> getAllCart(){
        return ResponseEntity.ok(ApiResponse.success(cartService.findAll()));
    }

    @PreAuthorize("hasRole('STAFF')")
    @PostMapping("")
    public ResponseEntity<ApiResponse<CartResponseDTO>> createCart(@Valid @RequestBody CreateCartDTO createCartDTO){
        return ResponseEntity.ok(ApiResponse.success(cartService.create(createCartDTO)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<CartResponseDTO>> updateCart(@Valid @RequestBody UpdateCartDTO updateCartDTO){
        return ResponseEntity.ok(ApiResponse.success(cartService.updateById( updateCartDTO)));
    }

    @PreAuthorize("hasRole('STAFF')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteCart(@PathVariable Long id){
        return ResponseEntity.ok(ApiResponse.success(ConstantUtils.DELETE_SUCCESSFULLY));
    }
}
