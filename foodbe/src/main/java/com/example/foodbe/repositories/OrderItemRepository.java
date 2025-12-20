package com.example.foodbe.repositories;

import com.example.foodbe.models.OrderItem;
import com.example.foodbe.models.OrderStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrderId(Long id);

    @Query("SELECT oi.product.id, oi.product.name, oi.product.imgProduct, " +
            "SUM(oi.price * oi.quantity) as revenue, SUM(oi.quantity) as quantity, COUNT(DISTINCT oi.order.id) as orders " +
            "FROM OrderItem oi " +
            "WHERE oi.order.status = :status " +
            "GROUP BY oi.product.id, oi.product.name, oi.product.imgProduct " +
            "ORDER BY revenue DESC")
    List<Object[]> findTopProductsByRevenue(@Param("status") OrderStatus status, Pageable pageable);

    @Query("SELECT oi.product.id, oi.product.name, oi.product.imgProduct, " +
            "SUM(oi.price * oi.quantity) as revenue, SUM(oi.quantity) as quantity, COUNT(DISTINCT oi.order.id) as orders " +
            "FROM OrderItem oi " +
            "WHERE oi.order.status = :status AND oi.order.createAt BETWEEN :startDate AND :endDate " +
            "GROUP BY oi.product.id, oi.product.name, oi.product.imgProduct " +
            "ORDER BY revenue DESC")
    List<Object[]> findTopProductsByRevenueInRange(
            @Param("status") OrderStatus status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );

    @Query("SELECT oi.product.category.id, oi.product.category.name, " +
            "SUM(oi.price * oi.quantity) as revenue, COUNT(DISTINCT oi.order.id) as orders " +
            "FROM OrderItem oi " +
            "WHERE oi.order.status = :status " +
            "GROUP BY oi.product.category.id, oi.product.category.name " +
            "ORDER BY revenue DESC")
    List<Object[]> getRevenueByCategory(@Param("status") OrderStatus status);

    @Query("SELECT oi.product.category.id, oi.product.category.name, " +
            "SUM(oi.price * oi.quantity) as revenue, COUNT(DISTINCT oi.order.id) as orders " +
            "FROM OrderItem oi " +
            "WHERE oi.order.status = :status AND oi.order.createAt BETWEEN :startDate AND :endDate " +
            "GROUP BY oi.product.category.id, oi.product.category.name " +
            "ORDER BY revenue DESC")
    List<Object[]> getRevenueByCategoryInRange(
            @Param("status") OrderStatus status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}
