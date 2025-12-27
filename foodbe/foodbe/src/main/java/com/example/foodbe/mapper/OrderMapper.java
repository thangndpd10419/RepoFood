package com.example.foodbe.mapper;

import com.example.foodbe.models.AppUser;
import com.example.foodbe.models.Order;
import com.example.foodbe.request.order.CreateOrderDTO;
import com.example.foodbe.request.order.UpdateOrderDTO;
import com.example.foodbe.response.order.OrderResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {
    public Order toEntity(CreateOrderDTO createOrderDTO, AppUser user){
        return Order.builder()
                .totalPrice(createOrderDTO.getTotalPrice())
                .status(createOrderDTO.getStatus())
                .table_number(createOrderDTO.getTable())
                .note(createOrderDTO.getNote())
                .user(user)
                .build();
    }

    public OrderResponseDTO toDto(Order order){
        return OrderResponseDTO.builder()
                .id(order.getId())
                .totalPrice(order.getTotalPrice())
                .status(order.getStatus())
                .table(order.getTable_number())
                .note(order.getNote())
                .build();
    }

    public Order updateDtoToEntity(UpdateOrderDTO updateOrderDTO, Order order){
        order.setStatus(updateOrderDTO.getStatus());
        return order;
    }
}
