package com.example.foodbe.utils;

import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

public class SortUtils2 {
    public static Sort getSort(String[] sort) {
        if (sort == null || sort.length == 0) {
            return Sort.unsorted(); // không sắp xếp
        }

        List<Sort.Order> orders = new ArrayList<>();

        for (String s : sort) {
            if (s.contains(",")) {
                // nhiều cột: "column,direction"
                String[] parts = s.split(",");
                if (parts.length == 2) {
                    orders.add(createOrder(parts[0].trim(), parts[1].trim()));
                }
            } else if (sort.length == 2) {
                // 1 cột: sort = [column, direction]
                orders.add(createOrder(sort[0].trim(), sort[1].trim()));
                break; // chỉ 1 cột
            }
        }

        return orders.isEmpty() ? Sort.unsorted() : Sort.by(orders);
    }

    private static Sort.Order createOrder(String property, String direction) {
        if ("desc".equalsIgnoreCase(direction)) {
            return Sort.Order.desc(property);
        }
        return Sort.Order.asc(property); // mặc định ASC
    }

}
