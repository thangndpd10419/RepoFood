package com.example.foodbe.request.category;

import com.example.foodbe.annotation.FormatWhitespace;
import com.example.foodbe.annotation.Trim;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateCategoryDTO {

    @NotNull
    private Long id;

    @FormatWhitespace
    @NotBlank(message = "{entity.name.required}")
    @Size(max = 30, message = "{entity.name.length}")
    private String name;

    @Trim
    @NotBlank(message = "{entity.name.required}")
    private String imgCategory;

    @NotNull(message = "{entity.number.not.null}")
    private Long userId;

}
