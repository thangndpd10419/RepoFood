package com.example.foodbe.services;

import com.example.foodbe.request.cart.CreateCartDTO;

import com.example.foodbe.request.cart.UpdateCartDTO;
import com.example.foodbe.response.cart.CartResponseDTO;

import java.util.List;

public interface CartService {
    List<CartResponseDTO> findAll();
    CartResponseDTO updateById(Long id, UpdateCartDTO updateCartDTO);
    CartResponseDTO create(CreateCartDTO createCartDTO);
    void deleteById(Long id);
    CartResponseDTO findById(Long id);
}
