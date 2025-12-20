package com.example.foodbe.services.impls;

import com.example.foodbe.models.AppUser;
import com.example.foodbe.payload.PageResponse;
import com.example.foodbe.repositories.UserRepository;
import com.example.foodbe.request.category.UpdateCategoryDTO;
import com.example.foodbe.response.category.CategoryResponseDTO;
import com.example.foodbe.request.category.CreateCategoryDTO;
import com.example.foodbe.exception_handler.NotFoundException;
import com.example.foodbe.mapper.CategoryMapper;
import com.example.foodbe.models.Category;
import com.example.foodbe.repositories.CategoryRepository;
import com.example.foodbe.repositories.ProductRepository;
import com.example.foodbe.services.CategoryService;
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
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final PageMapperUtils2 pageMapperUtils2;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Override
    public PageResponse<CategoryResponseDTO> findByNameContaining( String name, Pageable pageable) {
        Page<Category> page = categoryRepository.findByNameContaining(name, pageable);
        Function<Category, CategoryResponseDTO> mapper = category -> {
            Integer productCount = productRepository.countByCategoryId(category.getId());
            return categoryMapper.toDTO(category, productCount);
        };
        return pageMapperUtils2.toPageResponseDto(page,mapper);
    }

    @Override
    public CategoryResponseDTO findById(Long id) {
      Category category=  categoryRepository.findById(id)
               .orElseThrow(() -> new NotFoundException(ConstantUtils.ExceptionMessage.NOT_FOUND + id));
      Integer productCount = productRepository.countByCategoryId(id);
      return categoryMapper.toDTO(category, productCount);
    }

    @Override
    public CategoryResponseDTO create(CreateCategoryDTO createCategoryDTO) {
        AppUser user = userRepository.findById(createCategoryDTO.getUserId())
                .orElseThrow(()-> new NotFoundException(ConstantUtils.ExceptionMessage.NOT_FOUND+createCategoryDTO.getUserId()));
        Category category= categoryMapper.toEntity(createCategoryDTO,user );
        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toDTO(savedCategory, 0);
    }

    @Override
    public CategoryResponseDTO updateById(Long id, UpdateCategoryDTO updateCategoryDTO) {

        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ConstantUtils.ExceptionMessage.NOT_FOUND + id));
        categoryMapper.updateEntityFromDto(updateCategoryDTO,existingCategory);
        Category category= categoryRepository.save(existingCategory);
        Integer productCount = productRepository.countByCategoryId(id);
        return categoryMapper.toDTO(category, productCount);
    }

    @Override
    public void deleteById(Long id) {
       if (!categoryRepository.existsById(id)){
               throw new NotFoundException(ConstantUtils.ExceptionMessage.NOT_FOUND + id);}

        categoryRepository.deleteById(id);
    }

}
