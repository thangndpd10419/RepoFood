package com.example.foodbe.services.impls;

import com.example.foodbe.exception_handler.NotFoundException;
import com.example.foodbe.mapper.OrderItemMapper;
import com.example.foodbe.mapper.OrderMapper;
import com.example.foodbe.models.Order;
import com.example.foodbe.models.OrderItem;
import com.example.foodbe.models.Product;
import com.example.foodbe.repositories.OrderItemRepository;
import com.example.foodbe.repositories.OrderRepository;
import com.example.foodbe.repositories.ProductRepository;
import com.example.foodbe.request.order_item.CreateOrderItemDTO;
import com.example.foodbe.response.order.OrderResponseDTO;
import com.example.foodbe.response.order_item.OrderItemResponseDTO;
import com.example.foodbe.services.OrderItemService;
import com.example.foodbe.services.OrderService;
import com.example.foodbe.utils.ConstantUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderItemServiceImpl implements OrderItemService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderItemMapper orderItemMapper;
    private final ProductRepository productRepository;


    @Override
    public List<OrderItemResponseDTO> findOrderItem(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(()-> new NotFoundException(ConstantUtils.ExceptionMessage.NOT_FOUND+id));
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(id);
     return orderItems.stream().map( orderItemMapper::toDto).toList();
    }

    @Override
    public OrderItemResponseDTO create(CreateOrderItemDTO createOrderItemDTO) {
        Product product = productRepository.findById(createOrderItemDTO.getProductId())
                .orElseThrow(()-> new NotFoundException(ConstantUtils.ExceptionMessage.NOT_FOUND));
        Order order = orderRepository.findById(createOrderItemDTO.getOrderId())
                .orElseThrow(()-> new NotFoundException(ConstantUtils.ExceptionMessage.NOT_FOUND));
        OrderItem orderItem = orderItemMapper.toEntity(createOrderItemDTO,product, order);
         return orderItemMapper.toDto(orderItemRepository.save(orderItem));
    }

    @Override
    public void deleteById(Long id) {
       orderItemRepository.findById(id)
               .orElseThrow(()-> new NotFoundException(ConstantUtils.ExceptionMessage.NOT_FOUND));
       orderItemRepository.deleteById(id);
    }
}
