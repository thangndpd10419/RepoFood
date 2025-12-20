package com.example.foodbe.services.impls;

import com.example.foodbe.exception_handler.NotFoundException;
import com.example.foodbe.mapper.OrderMapper;
import com.example.foodbe.models.AppUser;
import com.example.foodbe.models.Order;
import com.example.foodbe.models.OrderItem;
import com.example.foodbe.models.OrderStatus;
import com.example.foodbe.models.Product;
import com.example.foodbe.payload.PageResponse;
import com.example.foodbe.repositories.OrderItemRepository;
import com.example.foodbe.repositories.OrderRepository;
import com.example.foodbe.repositories.ProductRepository;
import com.example.foodbe.repositories.UserRepository;
import com.example.foodbe.request.order.CreateOrderDTO;
import com.example.foodbe.request.order.UpdateOrderDTO;
import com.example.foodbe.response.order.OrderResponseDTO;
import com.example.foodbe.services.OrderService;
import com.example.foodbe.utils.ConstantUtils;
import com.example.foodbe.utils.PageMapperUtils2;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.foodbe.response.order_item.OrderItemResponseDTO;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderMapper orderMapper;
    private final PageMapperUtils2 pageMapperUtils2;

    @Override
    public PageResponse<OrderResponseDTO> findAll(Pageable pageable) {
       Page<Order> orderPage = orderRepository.findAll(pageable);
        Function<Order, OrderResponseDTO> mapper = orderMapper::toDto;
        return pageMapperUtils2.toPageResponseDto(orderPage, mapper);
    }

    @Override
    @Transactional
    public OrderResponseDTO create(CreateOrderDTO createOrderDTO) {
       AppUser user;

       if (createOrderDTO.getUserId() != null) {
           user = userRepository.findById(createOrderDTO.getUserId())
                   .orElseThrow(() -> new NotFoundException(ConstantUtils.ExceptionMessage.NOT_FOUND + createOrderDTO.getUserId()));
       } else {

           Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
           if (authentication == null) {
               throw new NotFoundException("Authentication is null - user not logged in");
           }
           String email = authentication.getName();
           if (email == null || email.isEmpty() || "anonymousUser".equals(email)) {
               throw new NotFoundException("Invalid authentication - email: " + email);
           }
           user = userRepository.findByEmail(email)
                   .orElseThrow(() -> new NotFoundException("User not found with email: " + email));
       }

       if (createOrderDTO.getStatus() == null) {
           createOrderDTO.setStatus(OrderStatus.PENDING);
       }

       BigDecimal totalPrice = createOrderDTO.getTotalPrice();
       if ((totalPrice == null || totalPrice.compareTo(BigDecimal.ZERO) == 0) && createOrderDTO.getOrderDetails() != null) {
           totalPrice = BigDecimal.ZERO;
           for (CreateOrderDTO.OrderDetailDTO detail : createOrderDTO.getOrderDetails()) {
               if (detail.getProductId() == null) {
                   throw new NotFoundException("Product ID cannot be null");
               }
               Product product = productRepository.findById(detail.getProductId())
                       .orElseThrow(() -> new NotFoundException("Product not found: " + detail.getProductId()));
               totalPrice = totalPrice.add(product.getPrice().multiply(BigDecimal.valueOf(detail.getQuantity())));
           }
           createOrderDTO.setTotalPrice(totalPrice);
       }

       Order order = orderRepository.save(orderMapper.toEntity(createOrderDTO, user));

       List<OrderItemResponseDTO> orderItemDTOs = new ArrayList<>();
       if (createOrderDTO.getOrderDetails() != null && !createOrderDTO.getOrderDetails().isEmpty()) {
           for (CreateOrderDTO.OrderDetailDTO detail : createOrderDTO.getOrderDetails()) {
               if (detail.getProductId() == null) {
                   throw new NotFoundException("Product ID cannot be null in order details");
               }
               Product product = productRepository.findById(detail.getProductId())
                       .orElseThrow(() -> new NotFoundException("Product not found: " + detail.getProductId()));

               OrderItem orderItem = OrderItem.builder()
                       .order(order)
                       .product(product)
                       .quantity(detail.getQuantity())
                       .price(product.getPrice())
                       .build();

               orderItemRepository.save(orderItem);

               orderItemDTOs.add(OrderItemResponseDTO.builder()
                       .quantity(detail.getQuantity())
                       .price(product.getPrice())
                       .imgProduct(product.getImgProduct())
                       .name(product.getName())
                       .build());
           }
       }

       return OrderResponseDTO.builder()
               .id(order.getId())
               .totalPrice(order.getTotalPrice())
               .status(order.getStatus())
               .table(order.getTable_number())
               .note(order.getNote())
               .userId(user.getId())
               .userName(user.getName())
               .createdAt(order.getCreateAt())
               .orderDetails(orderItemDTOs)
               .build();
    }

    @Override
    public OrderResponseDTO updateById(Long id, UpdateOrderDTO updateOrderDTO) {
        Order order = orderRepository.findById(id)
                .orElseThrow(()-> new NotFoundException(ConstantUtils.ExceptionMessage.NOT_FOUND+id));

        Order updated = orderRepository.save(orderMapper.updateDtoToEntity(updateOrderDTO, order));
        return orderMapper.toDto(updated);
    }

    @Override
    public void deleteById(Long id) {
        orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ConstantUtils.ExceptionMessage.NOT_FOUND+id));
        orderRepository.deleteById(id);
    }

    @Override
    public OrderResponseDTO findById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ConstantUtils.ExceptionMessage.NOT_FOUND + id));
        return orderMapper.toDto(order);
    }
}
