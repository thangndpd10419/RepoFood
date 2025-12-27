package com.example.foodbe.request.cart;

import com.example.foodbe.annotation.Trim;
import com.example.foodbe.utils.ConstantUtils;
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
    @Min(value = ConstantUtils.ValidateMessgae.QUANTITY_MIN, message = "{entity.number.positive}")
    @Max(value = ConstantUtils.ValidateMessgae.QUANTITY_MAX, message = "....max")
    private int quantity;

    @NotNull(message = "{entity.number.not.null}")
    private Long productId;
    @NotNull(message = "{entity.number.not.null}")
    private Long userId;
}
