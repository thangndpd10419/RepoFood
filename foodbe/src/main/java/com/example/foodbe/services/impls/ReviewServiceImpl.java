package com.example.foodbe.services.impls;

import com.example.foodbe.payload.PageResponse;
import com.example.foodbe.request.review.CreateReviewDto;
import com.example.foodbe.response.review.ReviewResponseDto;
import com.example.foodbe.request.review.UpdateReviewDto;
import com.example.foodbe.exception_handler.NotFoundException;
import com.example.foodbe.mapper.ReviewMapper;
import com.example.foodbe.models.AppUser;
import com.example.foodbe.models.Product;
import com.example.foodbe.models.Review;
import com.example.foodbe.repositories.ProductRepository;
import com.example.foodbe.repositories.ReviewRepository;
import com.example.foodbe.repositories.UserRepository;
import com.example.foodbe.services.ReviewService;
import com.example.foodbe.utils.ConstantUtils;
import com.example.foodbe.utils.PageMapperUtils2;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final PageMapperUtils2 pageMapperUtils2;

    @Override
    public PageResponse<ReviewResponseDto> findAll(Pageable pageable) {
        Page<Review> page = reviewRepository.findAll(pageable);
        Function<Review, ReviewResponseDto> mapper = reviewMapper::toDto;
        return pageMapperUtils2.toPageResponseDto(page, mapper);
    }

    @Override
    public ReviewResponseDto create(CreateReviewDto createReviewDto) {

        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        String email= authentication.getName();
        AppUser appUser= userRepository.findByEmail(email)
                .orElseThrow(()-> new NotFoundException(ConstantUtils.ExceptionMessage.NOT_FOUND +email));
        Product product= productRepository.findById(createReviewDto.getProductId())
                .orElseThrow(()-> new NotFoundException(ConstantUtils.ExceptionMessage.NOT_FOUND +createReviewDto.getProductId()));

        Review review= reviewMapper.toEntity(createReviewDto, appUser, product);
        return reviewMapper.toDto(reviewRepository.save(review));
    }

    @Override
    public ReviewResponseDto updateById(Long id, UpdateReviewDto updateReviewDto) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(()-> new NotFoundException(ConstantUtils.ExceptionMessage.NOT_FOUND+id));
        reviewMapper.updateDtoFromEntity(review, updateReviewDto);

        return reviewMapper.toDto(reviewRepository.save(review));
    }

    @Override
    public void deleteById(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(()-> new NotFoundException(ConstantUtils.ExceptionMessage.NOT_FOUND +id));
        reviewRepository.deleteById(id);
    }
}
