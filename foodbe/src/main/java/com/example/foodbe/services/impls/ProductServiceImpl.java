package com.example.foodbe.services.impls;

import com.example.foodbe.payload.PageResponse;
import com.example.foodbe.request.product.CreateProductDTO;
import com.example.foodbe.response.product.ProductResponseDTO;
import com.example.foodbe.request.product.UpdateProductDTO;
import com.example.foodbe.exception_handler.NotFoundException;
import com.example.foodbe.mapper.ProductMapper;
import com.example.foodbe.models.Category;
import com.example.foodbe.models.Product;
import com.example.foodbe.repositories.CategoryRepository;
import com.example.foodbe.repositories.ProductRepository;
import com.example.foodbe.response.user.UserResponseDTO;
import com.example.foodbe.services.ProductService;
import com.example.foodbe.utils.ConstantUtils;
import com.example.foodbe.utils.PageMapperUtils2;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;
    private final PageMapperUtils2 pageMapperUtils2;

    @Override
    public PageResponse<ProductResponseDTO> findByNameContaining(String name, Pageable pageable) {
        Page<Product> page = productRepository.findByNameContaining(name, pageable);
        Function<Product, ProductResponseDTO> mapper = productMapper::toDto;
        return pageMapperUtils2.toPageResponseDto(page, mapper);
    }

    @Override
    public ProductResponseDTO findById(Long id) {
        Product product= productRepository.findById(id)
                .orElseThrow(() ->new NotFoundException(ConstantUtils.ExceptionMessage.NOT_FOUND +id));
        return productMapper.toDto(product);
    }

    @Override
    public ProductResponseDTO create(CreateProductDTO createProductDTO) {
        Category category= categoryRepository.findById(createProductDTO.getCategoryId())
                .orElseThrow(() -> new NotFoundException(ConstantUtils.ExceptionMessage.NOT_FOUND + createProductDTO.getCategoryId()));
        Product product= productMapper.toEntity(createProductDTO,category);

        return productMapper.toDto(productRepository.save(product));
    }

    @Override
    public ProductResponseDTO updateById(Long id,UpdateProductDTO updateProductDTO) {
       Product product= productRepository.findById(id)
               .orElseThrow(()-> new NotFoundException(ConstantUtils.ExceptionMessage.NOT_FOUND+id));
       productMapper.UpdateEntityFromDto(updateProductDTO,product);

       if (updateProductDTO.getCategoryId() != null) {
           Category category = categoryRepository.findById(updateProductDTO.getCategoryId())
                   .orElseThrow(() -> new NotFoundException(ConstantUtils.ExceptionMessage.NOT_FOUND + updateProductDTO.getCategoryId()));
           product.setCategory(category);
       }

       productRepository.save(product);
        return productMapper.toDto(product);
    }

    @Override
    public void deleteById(Long id) {
       if(!productRepository.existsById(id))
                throw new NotFoundException(ConstantUtils.ExceptionMessage.NOT_FOUND+id);
        productRepository.deleteById(id);
    }
}
