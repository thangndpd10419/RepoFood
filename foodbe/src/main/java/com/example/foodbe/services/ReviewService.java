package com.example.foodbe.services;

import com.example.foodbe.payload.PageResponse;
import com.example.foodbe.request.review.CreateReviewDto;
import com.example.foodbe.response.review.ReviewResponseDto;
import com.example.foodbe.request.review.UpdateReviewDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReviewService {

    PageResponse<ReviewResponseDto> findAll(Pageable pageable);

    ReviewResponseDto create(CreateReviewDto createReviewDto);
    ReviewResponseDto updateById(Long id,UpdateReviewDto updateReviewDto);
    void deleteById(Long id);

}
