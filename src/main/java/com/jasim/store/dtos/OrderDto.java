package com.jasim.store.dtos;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDto {
    private long id;
    private List<OrderItemDto> items;
    private LocalDateTime createdAt;
    private String status;
    private BigDecimal totalPrice;
}
