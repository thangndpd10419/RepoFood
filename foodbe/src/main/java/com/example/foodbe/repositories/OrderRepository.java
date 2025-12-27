package com.example.foodbe.repositories;

import com.example.foodbe.models.Order;
import com.example.foodbe.models.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findAll(Pageable pageable);

    Long countByStatus(OrderStatus status);


    // total from order where status = completed;
    @Query("SELECT COALESCE(SUM(o.totalPrice), 0) FROM Order o WHERE o.status = :status")
    BigDecimal sumTotalPriceByStatus(@Param("status") OrderStatus status);

    // total from order where create_at [ start, end ] and status = completed
    // tôtnrg daonh thu theo khoang thoi gian
    @Query("SELECT COALESCE(SUM(o.totalPrice), 0) FROM Order o WHERE o.createAt BETWEEN :startDate AND :endDate AND o.status = :status")
    BigDecimal sumRevenueByDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("status") OrderStatus status
    );


    // count(*) from order where create_at [start, end] : tong don hang
    @Query("SELECT COUNT(o) FROM Order o WHERE o.createAt BETWEEN :startDate AND :endDate")
    Long countOrdersByDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );


    // date , sum , count from order where create [start,end] and status =completed
    // group by create_at  select orderby date Asc.
    // doanh thu theo ngay ( băt đầu ngày, két thuc ngay, completed)
    @Query("SELECT FUNCTION('DATE', o.createAt) as date, SUM(o.totalPrice) as revenue, COUNT(o) as orders " +
            "FROM Order o " +
            "WHERE o.createAt BETWEEN :startDate AND :endDate AND o.status = :status " +
            "GROUP BY FUNCTION('DATE', o.createAt) " +
            "ORDER BY date ASC")
    List<Object[]> getRevenueByDay(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("status") OrderStatus status
    );


    // year month total count from order group by year, month
    // donah thu, tổng theo thời gian ( tháng)
    @Query("SELECT FUNCTION('YEAR', o.createAt) as year, FUNCTION('MONTH', o.createAt) as month, " +
            "SUM(o.totalPrice) as revenue, COUNT(o) as orders " +
            "FROM Order o " +
            "WHERE o.createAt BETWEEN :startDate AND :endDate AND o.status = :status " +
            "GROUP BY FUNCTION('YEAR', o.createAt), FUNCTION('MONTH', o.createAt) " +
            "ORDER BY year ASC, month ASC")
    List<Object[]> getRevenueByMonth(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("status") OrderStatus status
    );

    // năm
    @Query("SELECT FUNCTION('YEAR', o.createAt) as year, SUM(o.totalPrice) as revenue, COUNT(o) as orders " +
            "FROM Order o " +
            "WHERE o.status = :status " +
            "GROUP BY FUNCTION('YEAR', o.createAt) " +
            "ORDER BY year ASC")
    List<Object[]> getRevenueByYear(@Param("status") OrderStatus status);

    List<Order> findTop10ByOrderByCreateAtDesc();
}
