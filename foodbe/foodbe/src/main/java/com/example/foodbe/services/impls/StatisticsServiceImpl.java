package com.example.foodbe.services.impls;

import com.example.foodbe.exception_handler.exception.InvalidDateRangeException;
import com.example.foodbe.mapper.RevenueMapper;
import com.example.foodbe.models.OrderStatus;
import com.example.foodbe.repositories.*;
import com.example.foodbe.response.statistics.*;
import com.example.foodbe.services.StatisticsService;
import com.example.foodbe.utils.ConstantUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Validated
public class StatisticsServiceImpl implements StatisticsService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final RevenueMapper revenueMapper;

    // khi query khong tren entity, hivernate khogn map duoc sang entity nen
    // nó coi mỗi cột trong bảng là 1 object
    @Override
    public DashboardStatsDTO getDashboardStats() {
        LocalDateTime now = LocalDateTime.now();

        //băt đầu từ 00:.. hôm nay
        LocalDateTime startOfToday = now.toLocalDate().atStartOfDay();
        //kêt thuc hôm nay
        LocalDateTime endOfToday = now.toLocalDate().atTime(LocalTime.MAX);
        // băt đầu hôm qu
        LocalDateTime startOfYesterday = startOfToday.minusDays(1);
        //kêt thuc hôm qua
        LocalDateTime endOfYesterday = endOfToday.minusDays(1);
        //băt đầu thang này từ ngay 1, 00:...
        LocalDateTime startOfMonth = now.toLocalDate().withDayOfMonth(1).atStartOfDay();
        // băt đâu thang trươc
        LocalDateTime startOfLastMonth = startOfMonth.minusMonths(1);
        //kêt thuc thang trươc
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
        // kiểm tra từ start đen end là bao nheu ngay
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
        if (daysBetween > 90) {
            throw new InvalidDateRangeException(ConstantUtils.ExceptionMessage.DATERANGE);
        }
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);
        List<Object[]> listO= orderRepository.getRevenueByDay(start,end, OrderStatus.COMPLETED);

        return listO.stream().map(o-> revenueMapper.toDtoByDate(o)).toList();
    }

    @Override
    public List<RevenueByMonthDTO> getRevenueByMonth(int year) {
        LocalDateTime start = LocalDate.of(year, 1, 1).atStartOfDay();
        LocalDateTime end = LocalDate.of(year, 12, 31).atTime(LocalTime.MAX);
        List<Object[]> results = orderRepository.getRevenueByMonth(start, end, OrderStatus.COMPLETED);

        return results.stream().map(o-> revenueMapper.toDtoByMonth(o)).toList();
    }

    @Override
    public List<RevenueByYearDTO> getRevenueByYear() {
        List<Object[]> results = orderRepository.getRevenueByYear(OrderStatus.COMPLETED);
        return results.stream().map(o->revenueMapper.toDtoByYear(o)).toList();
    }

    @Override
    public List<RevenueByCategoryDTO> getRevenueByCategory() {
        List<Object[]> results = orderItemRepository.getRevenueByCategory(OrderStatus.COMPLETED);
        return results.stream().map(o-> revenueMapper.toDtoByCategory(o)).toList();
    }

    @Override
    public List<TopProductDTO> getTopProducts(int limit) {

        Pageable page= PageRequest.of(0, limit);
        List<Object[]> objs=orderItemRepository.findTopProductsByRevenue(OrderStatus.COMPLETED, page);
        return objs.stream().map(o-> revenueMapper.toDtoByTop(o)).toList();
    }

    @Override
    public List<TopProductDTO> getTopProductsInRange(LocalDate startDate, LocalDate endDate, int limit) {
        Pageable page= PageRequest.of(0, limit);
        List<Object[]> objs= orderItemRepository.findTopProductsByRevenueInRange(OrderStatus.COMPLETED, startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX), page);
        return objs.stream().map(o-> revenueMapper.toDtoByTop(o)).toList();
    }




    private Double calculateGrowth(BigDecimal current, BigDecimal previous) {
        if (previous == null || previous.compareTo(BigDecimal.ZERO) == 0) {
            return current != null && current.compareTo(BigDecimal.ZERO) > 0 ? 100.0 : 0.0;
            // trước =0
            // nếu sau >0 => tnawg 100%
            // sau = 0 tăng 0%
        }
        if (current == null) {
            return -100.0;
            // trước !=0 => sau =0 => -100%
        }


        // bình thường: trươc và sau !=0
        // ==> (sau - trước ) / trước  = % tăng trưởng
        return current.subtract(previous)
                .divide(previous, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();
    }
}
