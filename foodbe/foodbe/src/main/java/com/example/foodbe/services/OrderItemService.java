package com.example.foodbe.services;

import com.example.foodbe.request.order_item.CreateOrderItemDTO;
import com.example.foodbe.response.order.OrderResponseDTO;
import com.example.foodbe.response.order_item.OrderItemResponseDTO;

import java.util.List;

public interface OrderItemService {
    List<OrderItemResponseDTO> findOrderItem(Long id);
    OrderItemResponseDTO create(CreateOrderItemDTO createOrderItemDTO);
    void deleteById(Long id);
}
