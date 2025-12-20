package com.example.foodbe.request.cart;

import com.example.foodbe.annotation.Trim;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateCartDTO {

    @NotNull(message = "{entity.number.not.null}")
    @Min(value = 1, message = "{entity.number.positive}")
    @Max(value = 50, message = "....")
    private int quantity;

    @NotNull(message = "...not null")
    private Long productId;
    @NotNull(message = "...not null")
    private Long userId;
}
