package com.example.foodbe.mapper;

import com.example.foodbe.models.Order;
import com.example.foodbe.models.OrderItem;
import com.example.foodbe.models.Product;
import com.example.foodbe.request.order_item.CreateOrderItemDTO;
import com.example.foodbe.response.order_item.OrderItemResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class OrderItemMapper {
    public OrderItem toEntity(CreateOrderItemDTO createOrderItemDTO, Product product, Order order){
        return OrderItem.builder()
                .quantity(createOrderItemDTO.getQuantity())
                .price(createOrderItemDTO.getPrice())
                .product(product)
                .order(order)
                .build();
    }

    public OrderItemResponseDTO toDto(OrderItem orderItem){
        return OrderItemResponseDTO.builder()
                .quantity(orderItem.getQuantity())
                .price(orderItem.getPrice())
                .imgProduct(orderItem.getProduct().getImgProduct())
                .name(orderItem.getProduct().getName())
                .build();
    }

}
