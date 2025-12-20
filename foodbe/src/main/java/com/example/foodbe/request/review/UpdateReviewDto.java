package com.example.foodbe.request.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateReviewDto {

    @Min(value = 1, message = "{entity.size.min}")
    @Max(value = 5, message = "{entity.size.max}")
    private Integer rating;

    @NotBlank(message = "{entity.name.required}")
    @Size(max = 500, message = "{entity.size.max}")
    private String comment;

}
