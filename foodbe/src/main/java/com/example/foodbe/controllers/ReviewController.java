package com.example.foodbe.controllers;

import com.example.foodbe.payload.PageResponse;
import com.example.foodbe.request.review.CreateReviewDto;
import com.example.foodbe.response.review.ReviewResponseDto;
import com.example.foodbe.request.review.UpdateReviewDto;
import com.example.foodbe.payload.ApiResponse;
import com.example.foodbe.services.ReviewService;
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
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Validated
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("")
    public ResponseEntity<ApiResponse<PageResponse<ReviewResponseDto>>> getReviews(
            @Min(value = ConstantUtils.Page.MIN_CURRENT_PAGE, message =  ConstantUtils.Page.PAGE_MIN_MSG)
            @RequestParam(defaultValue = "" + ConstantUtils.Page.DEFAULT_CURRENT_PAGE) Integer page,

            @Min(value = ConstantUtils.Page.MIN_PAGE_SIZE, message = ConstantUtils.Page.SIZE_MIN_MSG)
            @Max(value = ConstantUtils.Page.MAX_PAGE_SIZE, message = ConstantUtils.Page.SIZE_MAX_MSG)
            @RequestParam(defaultValue = "" + ConstantUtils.Page.DEFAULT_PAGE_SIZE) Integer size,

            @RequestParam(defaultValue = "id,asc") String[] sort
    ){
        Pageable pageable = PageRequest.of(page, size, SortUtils2.getSort(sort));
        return ResponseEntity.ok(ApiResponse.success(reviewService.findAll(pageable)));
    }

    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<ReviewResponseDto>> create(@Valid @RequestBody CreateReviewDto createReviewDto){
        return ResponseEntity.ok(ApiResponse.success(reviewService.create(createReviewDto)));
    }

    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ReviewResponseDto>> updateById(@PathVariable Long id, @Valid @RequestBody UpdateReviewDto updateReviewDto){
        return ResponseEntity.ok(ApiResponse.success(reviewService.updateById(id,updateReviewDto)));
    }

    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteById(@PathVariable Long id){
        reviewService.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success(ConstantUtils.DELETE_SUCCESSFULLY +id));
    }

}
