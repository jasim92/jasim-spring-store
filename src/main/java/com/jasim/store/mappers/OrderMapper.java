package com.jasim.store.mappers;

import com.jasim.store.dtos.OrderDto;
import com.jasim.store.entities.Order;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderDto toDto(Order order);
}
