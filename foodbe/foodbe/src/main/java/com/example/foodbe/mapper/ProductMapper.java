package com.example.foodbe.mapper;


import com.example.foodbe.request.product.CreateProductDTO;
import com.example.foodbe.response.product.ProductResponseDTO;
import com.example.foodbe.request.product.UpdateProductDTO;
import com.example.foodbe.models.Category;
import com.example.foodbe.models.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductMapper {

    private final CategoryMapper categoryMapper;

    public ProductResponseDTO toDto(Product product){
        return ProductResponseDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .quantity(product.getQuality())
                .price(product.getPrice())
                .imgProduct(product.getImgProduct())
                .description(product.getDescription())
                .idCategory(product.getCategory().getId())
                .build();
    }

    public Product toEntity(CreateProductDTO createProductDTO,Category category){

        return Product.builder()
                .name(createProductDTO.getName())
                .quality(createProductDTO.getQuantity())
                .price(createProductDTO.getPrice())
                .imgProduct(createProductDTO.getImgProduct())
                .description(createProductDTO.getDescription())
                .category(category)
                .build();
    }

    public void UpdateEntityFromDto(UpdateProductDTO updateProductDTO, Product product){
        if(updateProductDTO == null || product==null ) return;
        product.setName(updateProductDTO.getName());
        product.setQuality(updateProductDTO.getQuantity());
        product.setImgProduct(updateProductDTO.getImgProduct());
        product.setPrice(updateProductDTO.getPrice());
        product.setDescription(updateProductDTO.getDescription());
    }



}
