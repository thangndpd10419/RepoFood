package com.example.foodbe.request.product;

import com.example.foodbe.annotation.FormatWhitespace;
import com.example.foodbe.annotation.Trim;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

//import javax.validation.constraints.NotBlank;
//import javax.validation.constraints.NotNull;
//import javax.validation.constraints.Positive;
//import javax.validation.constraints.Size;
import java.math.BigDecimal;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateProductDTO {

    @FormatWhitespace
    @NotBlank(message = "{entity.name.required}")
    private String name;


    @NotNull(message = "{entity.number.not.null}")
    @Positive(message = "{entity.number.positive}")
    private int quantity;

    @NotNull(message = "{entity.number.not.null}")
    @Positive(message = "{entity.number.positive}")
    private BigDecimal price;

    @Trim
    @NotBlank(message = "{entity.name.required}")
    private String imgProduct;

    @Size(max = 1000, message = "{entity.size.max}")
    private String description;

    @NotNull(message = "{entity.number.not.null}")
    private Long categoryId;
}
