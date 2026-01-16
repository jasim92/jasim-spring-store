package com.jasim.store.services;

import com.jasim.store.dtos.OrderDto;
import com.jasim.store.exceptions.NoOrderFoundException;
import com.jasim.store.mappers.OrderMapper;
import com.jasim.store.repositories.OrderRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class OrderService {
    private AuthService authService;
    private OrderRepository orderRepository;
    private OrderMapper orderMapper;

    public List<OrderDto> getAllOrders(){
        var user = authService.getCurrentUser();
        var orders = orderRepository.getAllByCustomer(user);
        return orders.stream().map(orderMapper::toDto).toList();
    }

    public OrderDto getOrder(Long orderId){
        var user = authService.getCurrentUser();
        var order = orderRepository.findOrderByItems(orderId)
                .orElseThrow(NoOrderFoundException::new);

        if (!order.isPlacedBy(user)){
            throw new AccessDeniedException("You dont have Access to this order");
        }

        return orderMapper.toDto(order);
    }

}
