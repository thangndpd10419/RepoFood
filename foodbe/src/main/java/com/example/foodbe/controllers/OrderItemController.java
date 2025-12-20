package com.example.foodbe.controllers;

import com.example.foodbe.payload.ApiResponse;
import com.example.foodbe.request.order_item.CreateOrderItemDTO;
import com.example.foodbe.response.order_item.OrderItemResponseDTO;
import com.example.foodbe.services.OrderItemService;
import com.example.foodbe.utils.ConstantUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orderItem")
@RequiredArgsConstructor
@Validated
public class OrderItemController {
    private final OrderItemService orderItemService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<List<OrderItemResponseDTO>>> getAllByOrderId(@PathVariable Long id){
        return ResponseEntity.ok(ApiResponse.success(orderItemService.findOrderItem(id)));
    }

    @PostMapping("")
    public ResponseEntity<ApiResponse<OrderItemResponseDTO>> create(@Valid @RequestBody CreateOrderItemDTO createOrderItemDTO){
        return ResponseEntity.ok(ApiResponse.success(orderItemService.create(createOrderItemDTO)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteById(@PathVariable Long id){
        orderItemService.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success(ConstantUtils.DELETE_SUCCESSFULLY));
    }
}
