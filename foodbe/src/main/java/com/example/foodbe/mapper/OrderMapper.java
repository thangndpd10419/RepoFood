package com.example.foodbe.mapper;

import com.example.foodbe.models.AppUser;
import com.example.foodbe.models.Order;
import com.example.foodbe.models.OrderItem;
import com.example.foodbe.repositories.OrderItemRepository;
import com.example.foodbe.request.order.CreateOrderDTO;
import com.example.foodbe.request.order.UpdateOrderDTO;
import com.example.foodbe.response.order.OrderResponseDTO;
import com.example.foodbe.response.order_item.OrderItemResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderMapper {
    private final OrderItemRepository orderItemRepository;
    private final OrderItemMapper orderItemMapper;

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

        List<OrderItem> orderItems = orderItemRepository.findByOrderId(order.getId());
        List<OrderItemResponseDTO> orderItemDTOs = orderItems.stream()
                .map(orderItemMapper::toDto)
                .collect(Collectors.toList());

        return OrderResponseDTO.builder()
                .id(order.getId())
                .totalPrice(order.getTotalPrice())
                .status(order.getStatus())
                .table(order.getTable_number())
                .note(order.getNote())
                .userId(order.getUser() != null ? order.getUser().getId() : null)
                .userName(order.getUser() != null ? order.getUser().getName() : null)
                .createdAt(order.getCreateAt())
                .orderDetails(orderItemDTOs)
                .build();
    }

    public Order updateDtoToEntity(UpdateOrderDTO updateOrderDTO, Order order){
        order.setStatus(updateOrderDTO.getStatus());
        return order;
    }
}
