package com.example.foodbe.services.impls;

import com.example.foodbe.exception_handler.NotFoundException;
import com.example.foodbe.mapper.OrderMapper;
import com.example.foodbe.models.AppUser;
import com.example.foodbe.models.Order;
import com.example.foodbe.payload.PageResponse;
import com.example.foodbe.repositories.OrderRepository;
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

import java.util.List;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
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
       AppUser user = userRepository.findById(createOrderDTO.getUserId())
               .orElseThrow(()-> new NotFoundException(ConstantUtils.ExceptionMessage.NOT_FOUND+createOrderDTO.getUserId()));
       Order order = orderRepository.save(orderMapper.toEntity(createOrderDTO, user));
       return orderMapper.toDto(order);
    }

    @Override
    public OrderResponseDTO updateById( UpdateOrderDTO updateOrderDTO) {
        Order order = orderRepository.findById(updateOrderDTO.getId())
                .orElseThrow(()-> new NotFoundException(ConstantUtils.ExceptionMessage.NOT_FOUND+updateOrderDTO.getId()));

        Order updated = orderRepository.save(orderMapper.updateDtoToEntity(updateOrderDTO, order));
        return orderMapper.toDto(updated);
    }

    @Override
    public void deleteById(Long id) {
        orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ConstantUtils.ExceptionMessage.NOT_FOUND+id));
        orderRepository.deleteById(id);
    }


}
