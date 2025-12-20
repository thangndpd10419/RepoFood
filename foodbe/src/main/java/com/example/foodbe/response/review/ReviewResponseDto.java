package com.example.foodbe.response.review;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class ReviewResponseDto {

    private Long id;
    private int rating;
    private String comment;

    private Long userId;
    private String userName;

    private Long productId;
    private String productName;

    private LocalDateTime created_at;
    private LocalDateTime updated_at;
}
