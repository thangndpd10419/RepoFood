package com.example.foodbe.request.order;


import com.example.foodbe.models.OrderStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateOrderDTO {

    @NotNull(message = "{entity.number.not.null}")
    private Long id;
    @NotNull(message = "{entity.number.not.null}")
    private BigDecimal totalPrice;
    private OrderStatus status;
    @NotNull(message = "{entity.number.not.null}")
    private int table;

    private String note;
    @NotNull(message = "{entity.number.not.null}")
    private Long userId;
}
