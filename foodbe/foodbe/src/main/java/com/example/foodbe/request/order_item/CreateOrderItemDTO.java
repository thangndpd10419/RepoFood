package com.example.foodbe.request.order_item;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateOrderItemDTO {

    @NotNull(message = "{entity.number.not.null}")
    private int quantity;
    @NotNull(message = "{entity.number.not.null}")
    private BigDecimal price;
    @NotNull(message = "{entity.number.not.null}")
    private Long productId;
    @NotNull(message = "{entity.number.not.null}")
    private Long OrderId;
}
