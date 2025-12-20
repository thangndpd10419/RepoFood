package com.example.foodbe.controllers;

import com.example.foodbe.payload.PageResponse;
import com.example.foodbe.request.product.CreateProductDTO;
import com.example.foodbe.response.product.ProductResponseDTO;
import com.example.foodbe.request.product.UpdateProductDTO;
import com.example.foodbe.payload.ApiResponse;
import com.example.foodbe.services.ProductService;
import com.example.foodbe.utils.ConstantUtils;
import com.example.foodbe.utils.SortUtils2;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Validated
public class ProductController {
    private final ProductService productService;

    @GetMapping("")
    public ResponseEntity<ApiResponse<PageResponse<ProductResponseDTO>>> getProducts(
        @Min(value = ConstantUtils.Page.MIN_CURRENT_PAGE, message =  ConstantUtils.Page.PAGE_MIN_MSG)
        @RequestParam(defaultValue = "" + ConstantUtils.Page.DEFAULT_CURRENT_PAGE) Integer page,

        @Min(value = ConstantUtils.Page.MIN_PAGE_SIZE, message = ConstantUtils.Page.SIZE_MIN_MSG)
        @Max(value = ConstantUtils.Page.MAX_PAGE_SIZE, message = ConstantUtils.Page.SIZE_MAX_MSG)
        @RequestParam(defaultValue = "" + ConstantUtils.Page.DEFAULT_PAGE_SIZE) Integer size,

        @RequestParam(defaultValue = "id,asc") String[] sort,
        @RequestParam(defaultValue = "", required = false) String name
    ){
        Pageable pageable = PageRequest.of(page, size, SortUtils2.getSort(sort));
        return ResponseEntity.ok(ApiResponse.success(productService.findByNameContaining(name, pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponseDTO>> getById( @PathVariable Long id){
        return ResponseEntity.ok(ApiResponse.success(productService.findById(id)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponseDTO>> create(@Valid @RequestBody CreateProductDTO createProductDTO){
        return ResponseEntity.ok(ApiResponse.success(productService.create(createProductDTO)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponseDTO>> updateById(@PathVariable Long id, @Valid @RequestBody UpdateProductDTO updateProductDTO){
        return ResponseEntity.ok(ApiResponse.success(productService.updateById(id, updateProductDTO)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteById(@PathVariable Long id){
        productService.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success(ConstantUtils.DELETE_SUCCESSFULLY +id));
    }

}
