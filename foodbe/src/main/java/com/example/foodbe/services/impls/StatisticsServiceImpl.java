package com.example.foodbe.services.impls;

import com.example.foodbe.models.OrderStatus;
import com.example.foodbe.repositories.*;
import com.example.foodbe.response.statistics.*;
import com.example.foodbe.services.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public DashboardStatsDTO getDashboardStats() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfToday = now.toLocalDate().atStartOfDay();
        LocalDateTime endOfToday = now.toLocalDate().atTime(LocalTime.MAX);

        LocalDateTime startOfYesterday = startOfToday.minusDays(1);
        LocalDateTime endOfYesterday = endOfToday.minusDays(1);

        LocalDateTime startOfMonth = now.toLocalDate().withDayOfMonth(1).atStartOfDay();
        LocalDateTime startOfLastMonth = startOfMonth.minusMonths(1);
        LocalDateTime endOfLastMonth = startOfMonth.minusSeconds(1);

        BigDecimal todayRevenue = orderRepository.sumRevenueByDateRange(
                startOfToday, endOfToday, OrderStatus.COMPLETED);

        BigDecimal yesterdayRevenue = orderRepository.sumRevenueByDateRange(
                startOfYesterday, endOfYesterday, OrderStatus.COMPLETED);

        BigDecimal monthRevenue = orderRepository.sumRevenueByDateRange(
                startOfMonth, endOfToday, OrderStatus.COMPLETED);

        BigDecimal lastMonthRevenue = orderRepository.sumRevenueByDateRange(
                startOfLastMonth, endOfLastMonth, OrderStatus.COMPLETED);

        Long totalOrders = orderRepository.count();
        Long todayOrders = orderRepository.countOrdersByDateRange(startOfToday, endOfToday);
        Long yesterdayOrders = orderRepository.countOrdersByDateRange(startOfYesterday, endOfYesterday);

        Long totalCustomers = userRepository.count();
        Long totalProducts = productRepository.count();
        Long totalCategories = categoryRepository.count();

        Double revenueGrowth = calculateGrowth(todayRevenue, yesterdayRevenue);
        Double orderGrowth = calculateGrowth(
                BigDecimal.valueOf(todayOrders),
                BigDecimal.valueOf(yesterdayOrders)
        );

        return DashboardStatsDTO.builder()
                .todayRevenue(todayRevenue != null ? todayRevenue : BigDecimal.ZERO)
                .yesterdayRevenue(yesterdayRevenue != null ? yesterdayRevenue : BigDecimal.ZERO)
                .monthRevenue(monthRevenue != null ? monthRevenue : BigDecimal.ZERO)
                .totalOrders(totalOrders)
                .todayOrders(todayOrders)
                .totalCustomers(totalCustomers)
                .totalProducts(totalProducts)
                .totalCategories(totalCategories)
                .revenueGrowth(revenueGrowth)
                .orderGrowth(orderGrowth)
                .build();
    }

    @Override
    public List<RevenueByDateDTO> getRevenueByDay(LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);

        List<Object[]> results = orderRepository.getRevenueByDay(start, end, OrderStatus.COMPLETED);
        List<RevenueByDateDTO> dtoList = new ArrayList<>();

        for (Object[] row : results) {
            dtoList.add(RevenueByDateDTO.builder()
                    .date(row[0].toString())
                    .revenue((BigDecimal) row[1])
                    .orders((Long) row[2])
                    .build());
        }

        return dtoList;
    }

    @Override
    public List<RevenueByDateDTO> getRevenueByMonth(int year) {
        LocalDateTime start = LocalDate.of(year, 1, 1).atStartOfDay();
        LocalDateTime end = LocalDate.of(year, 12, 31).atTime(LocalTime.MAX);

        List<Object[]> results = orderRepository.getRevenueByMonth(start, end, OrderStatus.COMPLETED);
        List<RevenueByDateDTO> dtoList = new ArrayList<>();

        for (Object[] row : results) {
            String month = "T" + row[1].toString();
            dtoList.add(RevenueByDateDTO.builder()
                    .date(month)
                    .revenue((BigDecimal) row[2])
                    .orders((Long) row[3])
                    .build());
        }

        return dtoList;
    }

    @Override
    public List<RevenueByDateDTO> getRevenueByYear() {
        List<Object[]> results = orderRepository.getRevenueByYear(OrderStatus.COMPLETED);
        List<RevenueByDateDTO> dtoList = new ArrayList<>();

        for (Object[] row : results) {
            dtoList.add(RevenueByDateDTO.builder()
                    .date(row[0].toString())
                    .revenue((BigDecimal) row[1])
                    .orders((Long) row[2])
                    .build());
        }

        return dtoList;
    }

    @Override
    public List<RevenueByCategoryDTO> getRevenueByCategory() {
        List<Object[]> results = orderItemRepository.getRevenueByCategory(OrderStatus.COMPLETED);
        List<RevenueByCategoryDTO> dtoList = new ArrayList<>();

        BigDecimal totalRevenue = results.stream()
                .map(row -> (BigDecimal) row[2])
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        for (Object[] row : results) {
            BigDecimal revenue = (BigDecimal) row[2];
            Double percentage = totalRevenue.compareTo(BigDecimal.ZERO) > 0
                    ? revenue.divide(totalRevenue, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .doubleValue()
                    : 0.0;

            dtoList.add(RevenueByCategoryDTO.builder()
                    .categoryId((Long) row[0])
                    .categoryName((String) row[1])
                    .revenue(revenue)
                    .orderCount((Long) row[3])
                    .percentage(percentage)
                    .build());
        }

        return dtoList;
    }

    @Override
    public List<TopProductDTO> getTopProducts(int limit) {
        List<Object[]> results = orderItemRepository.findTopProductsByRevenue(
                OrderStatus.COMPLETED,
                PageRequest.of(0, limit)
        );

        return mapToTopProductDTO(results);
    }

    @Override
    public List<TopProductDTO> getTopProductsInRange(LocalDate startDate, LocalDate endDate, int limit) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);

        List<Object[]> results = orderItemRepository.findTopProductsByRevenueInRange(
                OrderStatus.COMPLETED,
                start,
                end,
                PageRequest.of(0, limit)
        );

        return mapToTopProductDTO(results);
    }

    private List<TopProductDTO> mapToTopProductDTO(List<Object[]> results) {
        List<TopProductDTO> dtoList = new ArrayList<>();

        for (Object[] row : results) {
            dtoList.add(TopProductDTO.builder()
                    .productId((Long) row[0])
                    .productName((String) row[1])
                    .productImage((String) row[2])
                    .revenue((BigDecimal) row[3])
                    .quantitySold((Long) row[4])
                    .orderCount((Long) row[5])
                    .build());
        }

        return dtoList;
    }

    private Double calculateGrowth(BigDecimal current, BigDecimal previous) {
        if (previous == null || previous.compareTo(BigDecimal.ZERO) == 0) {
            return current != null && current.compareTo(BigDecimal.ZERO) > 0 ? 100.0 : 0.0;
        }
        if (current == null) {
            return -100.0;
        }

        return current.subtract(previous)
                .divide(previous, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();
    }
}
