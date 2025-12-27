package com.example.foodbe.mapper;

import com.example.foodbe.models.AppUser;
import com.example.foodbe.models.Cart;
import com.example.foodbe.models.Product;
import com.example.foodbe.request.cart.CreateCartDTO;
import com.example.foodbe.request.cart.UpdateCartDTO;
import com.example.foodbe.response.cart.CartResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class CartMapper {
    public Cart toEntity(CreateCartDTO createCartDTO, Product product, AppUser user){
        return Cart.builder()
                .quantity(createCartDTO.getQuantity())
                .product(product)
                .user(user)
                .build();
    }

    public CartResponseDTO toDto(Cart cart){
        return CartResponseDTO.builder()
                .id(cart.getId())
                .name(cart.getProduct().getName())
                .quantity(cart.getQuantity())
                .price(cart.getProduct().getPrice())
                .imgProduct(cart.getProduct().getImgProduct())
                .build();
    }

    public Cart updateCart(Cart cart, UpdateCartDTO updateCartDTO){
        cart.setQuantity(updateCartDTO.getQuantity());
        return cart;
    }
}
