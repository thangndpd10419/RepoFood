package com.example.foodbe.mapper;


import com.example.foodbe.response.statistics.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class RevenueMapper {
    public RevenueByDateDTO toDtoByDate(Object[] row) {
        return RevenueByDateDTO.builder()
                .date(row[0].toString())
                .revenue((BigDecimal) row[1])
                .orders((Long) row[2])
                .build();
    }

    public RevenueByMonthDTO toDtoByMonth(Object[] row){
        return  RevenueByMonthDTO.builder()
                .year((Integer) row[0])
                .month((Integer) row[1])
                .revenue((BigDecimal) row[2])
                .orders((Long) row[3])
                .build();
    }

    public RevenueByYearDTO toDtoByYear(Object[] row){
        return RevenueByYearDTO.builder()
                .year((Integer) row[0])       // nếu Hibernate trả Long, có thể convert: ((Long)row[0]).intValue()
                .revenue((BigDecimal) row[1])
                .orders((Long) row[2])
                .build();
    }

    public RevenueByCategoryDTO toDtoByCategory(Object[] row){
        return RevenueByCategoryDTO.builder()
                .categoryId((Long) row[0])
                .categoryName((String) row[1])
                .revenue((BigDecimal) row[2])
                .orderCount((Long) row[3])
                .build();
    }

    public TopProductDTO toDtoByTop(Object[] row){
        return TopProductDTO.builder()
                .productId((Long) row[0])
                .productName((String) row[1])
                .imgProduct((String) row[2])
                .revenue((BigDecimal) row[3])
                .quantity(((Number) row[4]).longValue()) // SUM trả về BigInteger/Long
                .orders(((Number) row[5]).longValue())   // COUNT trả về Long/BigInteger
                .build();
    }
}
