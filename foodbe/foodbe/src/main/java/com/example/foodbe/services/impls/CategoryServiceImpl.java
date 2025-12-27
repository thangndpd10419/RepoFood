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

//    @Override
//    public List<CategoryResponseDTO> findAll() {
//        List<Category> categories = categoryRepository.findAll();
//
//        return categories.stream()
//                .map(category->categoryMapper.toDTO(category))
//                .collect(Collectors.toList());
//
//    }


    @Override
    public PageResponse<CategoryResponseDTO> findByNameContaining( String name, Pageable pageable) {
        Page<Category> page = categoryRepository.findByNameContaining(name, pageable);
        Function<Category, CategoryResponseDTO> mapper = categoryMapper::toDTO;
        return pageMapperUtils2.toPageResponseDto(page,mapper);
    }

    @Override
    public CategoryResponseDTO findById(Long id) {
      Category category=  categoryRepository.findById(id)
               .orElseThrow(() -> new NotFoundException(ConstantUtils.ExceptionMessage.NOT_FOUND + id));
     return categoryMapper.toDTO(category);
    }

    @Override
    public CategoryResponseDTO create(CreateCategoryDTO createCategoryDTO) {
        AppUser user = userRepository.findById(createCategoryDTO.getUserId())
                .orElseThrow(()-> new NotFoundException(ConstantUtils.ExceptionMessage.NOT_FOUND+createCategoryDTO.getUserId()));
        Category category= categoryMapper.toEntity(createCategoryDTO,user );
        return categoryMapper.toDTO( categoryRepository.save(category)) ;

    }

    @Override
    public CategoryResponseDTO updateById( UpdateCategoryDTO updateCategoryDTO) {
        // 1. Tìm entity cũ trong DB
        Category existingCategory = categoryRepository.findById(updateCategoryDTO.getId())
                .orElseThrow(() -> new NotFoundException(ConstantUtils.ExceptionMessage.NOT_FOUND + updateCategoryDTO.getId()));
        categoryMapper.updateEntityFromDto(updateCategoryDTO,existingCategory);
        Category category= categoryRepository.save(existingCategory);
        return categoryMapper.toDTO(category);
    }

    @Override
    public void deleteById(Long id) {
       if (!categoryRepository.existsById(id)){
               throw new NotFoundException(ConstantUtils.ExceptionMessage.NOT_FOUND + id);}
        // Xóa category nếu tồn tại
        categoryRepository.deleteById(id);
    }

}
