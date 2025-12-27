package com.example.foodbe.services.impls;

import com.example.foodbe.exception_handler.NotFoundException;
import com.example.foodbe.mapper.CartMapper;
import com.example.foodbe.models.AppUser;
import com.example.foodbe.models.Cart;
import com.example.foodbe.models.Product;
import com.example.foodbe.repositories.CartRepository;
import com.example.foodbe.repositories.ProductRepository;
import com.example.foodbe.repositories.UserRepository;
import com.example.foodbe.request.cart.CreateCartDTO;
import com.example.foodbe.request.cart.UpdateCartDTO;
import com.example.foodbe.response.cart.CartResponseDTO;
import com.example.foodbe.services.CartService;
import com.example.foodbe.utils.ConstantUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final CartMapper cartMapper;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Override
    public List<CartResponseDTO> findAll() {
        List<Cart> carts = cartRepository.findAll();
        return carts.stream()
                .map(cart -> cartMapper.toDto(cart))
                .collect(Collectors.toList());
    }

    @Override
    public CartResponseDTO updateById( UpdateCartDTO updateCartDTO) {
        Cart c = cartRepository.findById(updateCartDTO.getId())
                .orElseThrow(()-> new NotFoundException(ConstantUtils.ExceptionMessage.NOT_FOUND+updateCartDTO.getId()));
        Cart cart = cartRepository.save(cartMapper.updateCart(c,updateCartDTO));
        return cartMapper.toDto(cart);
    }

    @Override
    public CartResponseDTO create(CreateCartDTO createCartDTO) {
        AppUser user= userRepository.findById(createCartDTO.getUserId())
                .orElseThrow(()-> new NotFoundException(ConstantUtils.ExceptionMessage.NOT_FOUND+createCartDTO.getUserId()));
        Product product = productRepository.findById(createCartDTO.getProductId())
                .orElseThrow(()-> new NotFoundException(ConstantUtils.ExceptionMessage.NOT_FOUND+createCartDTO.getProductId()));

        Cart c = cartMapper.toEntity(createCartDTO, product, user);
        Cart cart = cartRepository.save(c);
        return cartMapper.toDto(cart);
    }

    @Override
    public void deleteById(Long id) {
        Cart cart = cartRepository.findById(id)
                .orElseThrow(()-> new NotFoundException(ConstantUtils.ExceptionMessage.NOT_FOUND+id));
        cartRepository.deleteById(id);
    }
}
