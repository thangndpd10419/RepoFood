package com.example.foodbe.request.review;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class CreateReviewDto {

    @Min(value = 1, message = "{entity.size.min}")
    @Max(value = 5, message = "{entity.size.max}")
    private Integer rating;

    @NotBlank(message = "{entity.name.required}")
    @Size(max = 500, message = "{entity.size.max}")
    private String comment;

    @NotNull(message = "{entity.number.not.null}")
    private Long productId;

}
