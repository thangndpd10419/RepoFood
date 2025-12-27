package com.example.foodbe.controllers;


import com.example.foodbe.payload.ApiResponse;
import com.example.foodbe.payload.PageResponse;
import com.example.foodbe.request.order.CreateOrderDTO;
import com.example.foodbe.request.order.UpdateOrderDTO;
import com.example.foodbe.response.order.OrderResponseDTO;
import com.example.foodbe.services.OrderService;
import com.example.foodbe.utils.ConstantUtils;
import com.example.foodbe.utils.SortUtils2;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@Validated
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;


    // order sẽ chứa it thông tin khong như product nên có thể trả về list

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("")
    public ResponseEntity<ApiResponse<PageResponse<OrderResponseDTO>>> getAll(
            @Min(value = ConstantUtils.Page.MIN_CURRENT_PAGE, message = ConstantUtils.Page.PAGE_MIN_MSG)
            @RequestParam(defaultValue = "" + ConstantUtils.Page.DEFAULT_CURRENT_PAGE) Integer page,

            @Max(value = ConstantUtils.Page.MAX_PAGE_SIZE, message = ConstantUtils.Page.SIZE_MAX_MSG)
            @Min(value = ConstantUtils.Page.MIN_PAGE_SIZE, message = ConstantUtils.Page.SIZE_MIN_MSG)
            @RequestParam(defaultValue = "" + ConstantUtils.Page.DEFAULT_PAGE_SIZE) Integer size,

            @RequestParam(defaultValue = "id,desc") String[] sort
    ){
        Pageable pageable = PageRequest.of(page, size, SortUtils2.getSort(sort));
        return ResponseEntity.ok(ApiResponse.success(orderService.findAll(pageable)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("")
    public ResponseEntity<ApiResponse<OrderResponseDTO>> createOrder (
            @Valid @RequestBody CreateOrderDTO createOrderDTO
            ){
        return ResponseEntity.ok(ApiResponse.success(orderService.create(createOrderDTO)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponseDTO>> update( @Valid @RequestBody UpdateOrderDTO updateOrderDTO){
        return ResponseEntity.ok(ApiResponse.success(orderService.updateById( updateOrderDTO)));
    }


    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("{id}")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable Long id){
                 orderService.deleteById(id);
        return  ResponseEntity.ok(ApiResponse.success(ConstantUtils.DELETE_SUCCESSFULLY));
    }

}
