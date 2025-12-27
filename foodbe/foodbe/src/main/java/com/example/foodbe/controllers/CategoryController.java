package com.example.foodbe.controllers;


import com.example.foodbe.payload.PageResponse;
import com.example.foodbe.response.category.CategoryResponseDTO;
import com.example.foodbe.request.category.CreateCategoryDTO;
import com.example.foodbe.request.category.UpdateCategoryDTO;
import com.example.foodbe.payload.ApiResponse;
import com.example.foodbe.services.CategoryService;
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

//import javax.validation.Valid;
//import javax.validation.constraints.Max;
//import javax.validation.constraints.Min;
//import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Validated
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("")
    public ResponseEntity<ApiResponse<PageResponse<CategoryResponseDTO>>> getCategories(
            @Min(value = ConstantUtils.Page.MIN_CURRENT_PAGE, message = ConstantUtils.Page.PAGE_MIN_MSG)
            @RequestParam(defaultValue = "" + ConstantUtils.Page.DEFAULT_CURRENT_PAGE) Integer page,

            @Max(value = ConstantUtils.Page.MAX_PAGE_SIZE, message = ConstantUtils.Page.SIZE_MAX_MSG)
            @Min(value = ConstantUtils.Page.MIN_PAGE_SIZE, message = ConstantUtils.Page.SIZE_MIN_MSG)
            @RequestParam(defaultValue = "" + ConstantUtils.Page.DEFAULT_PAGE_SIZE) Integer size,

            @RequestParam(defaultValue = "id,asc") String[] sort,
            @RequestParam(required = false, defaultValue = "") String name)
    {
        Pageable pageable = PageRequest.of(page,size, SortUtils2.getSort(sort));
        return ResponseEntity.ok(ApiResponse.success(categoryService.findByNameContaining(name, pageable)));
    }



    @GetMapping("/{id}")
    public  ResponseEntity<ApiResponse<CategoryResponseDTO>> getById( @PathVariable Long id){
        return ResponseEntity.ok(ApiResponse.success(categoryService.findById(id)));
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("")
    public ResponseEntity<ApiResponse<CategoryResponseDTO>> create(@Valid @RequestBody CreateCategoryDTO createCategoryDTO){
        return ResponseEntity.ok(ApiResponse.success(categoryService.create(createCategoryDTO)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponseDTO>> updateById(@Valid @RequestBody UpdateCategoryDTO updateCategoryDTO){
        return ResponseEntity.ok(ApiResponse.success(categoryService.updateById(updateCategoryDTO)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteById(@PathVariable Long id){
        categoryService.deleteById(id);
        return  ResponseEntity.ok(ApiResponse.success(ConstantUtils.DELETE_SUCCESSFULLY +id));
    }


}
