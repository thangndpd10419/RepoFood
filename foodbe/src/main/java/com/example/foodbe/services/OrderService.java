package com.example.foodbe.services;

import com.example.foodbe.models.Order;
import com.example.foodbe.payload.PageResponse;
import com.example.foodbe.request.order.CreateOrderDTO;
import com.example.foodbe.request.order.UpdateOrderDTO;
import com.example.foodbe.response.order.OrderResponseDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {
    PageResponse<OrderResponseDTO> findAll(Pageable pageable);

    OrderResponseDTO create (CreateOrderDTO createOrderDTO);

    OrderResponseDTO updateById(Long id, UpdateOrderDTO updateOrderDTO);

    void deleteById(Long id);

    OrderResponseDTO findById(Long id);
}
