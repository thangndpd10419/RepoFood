package com.example.foodbe.services.impls;

import com.example.foodbe.exception_handler.NotFoundException;
import com.example.foodbe.mapper.ProductMapper;
import com.example.foodbe.models.Category;
import com.example.foodbe.models.Product;
import com.example.foodbe.payload.PageResponse;
import com.example.foodbe.repositories.CategoryRepository;
import com.example.foodbe.repositories.ProductRepository;
import com.example.foodbe.request.product.CreateProductDTO;
import com.example.foodbe.request.product.UpdateProductDTO;
import com.example.foodbe.response.product.ProductResponseDTO;
import com.example.foodbe.services.ProductService;
import com.example.foodbe.services.ProductService2;
import com.example.foodbe.utils.ConstantUtils;
import com.example.foodbe.utils.PageMapperUtils2;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl2 implements ProductService2 {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;
    private final PageMapperUtils2 pageMapperUtils2;

    private final String UPLOAD_DIR = "E:/SpringTuHoc/foodbe/upload/";

    private String saveFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) return null;

        File folder = new File(UPLOAD_DIR);
        if (!folder.exists()) folder.mkdirs();

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename().replaceAll("\\s", "_");
        File dest = new File(folder, fileName);
        file.transferTo(dest);

        return fileName;
    }

    @Override
    public PageResponse<ProductResponseDTO> findByNameContaining(String name, Pageable pageable) {
        Page<Product> page = productRepository.findByNameContaining(name, pageable);

        return pageMapperUtils2.toPageResponseDto(page, productMapper::toDto);
    }

    @Override
    public ProductResponseDTO findById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ConstantUtils.ExceptionMessage.NOT_FOUND + id));
        return productMapper.toDto(product);
    }

    @Override
    public ProductResponseDTO create(CreateProductDTO createProductDTO, MultipartFile file) throws IOException {
        Category category = categoryRepository.findById(createProductDTO.getCategoryId())
                .orElseThrow(() -> new NotFoundException(ConstantUtils.ExceptionMessage.NOT_FOUND + createProductDTO.getCategoryId()));

        Product product = productMapper.toEntity(createProductDTO, category);

        String fileName = saveFile(file);
        product.setImgProduct(fileName);

        return productMapper.toDto(productRepository.save(product));
    }

    @Override
    public ProductResponseDTO updateById(Long id, UpdateProductDTO updateProductDTO, MultipartFile file) throws IOException {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ConstantUtils.ExceptionMessage.NOT_FOUND + id));

        productMapper.UpdateEntityFromDto(updateProductDTO, product);

        if (file != null && !file.isEmpty()) {
            String fileName = saveFile(file);
            product.setImgProduct(fileName);
        }

        productRepository.save(product);
        return productMapper.toDto(product);
    }

    @Override
    public void deleteById(Long id) {
        if (!productRepository.existsById(id))
            throw new NotFoundException(ConstantUtils.ExceptionMessage.NOT_FOUND + id);
        productRepository.deleteById(id);
    }
}
