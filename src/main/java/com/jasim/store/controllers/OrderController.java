package com.jasim.store.controllers;

import com.jasim.store.dtos.ErrorDto;
import com.jasim.store.dtos.OrderDto;
import com.jasim.store.exceptions.NoOrderFoundException;
import com.jasim.store.services.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/orders")
public class OrderController {

   private final OrderService orderService;

    @GetMapping
    public List<OrderDto> getAllOrders(){
        return orderService.getAllOrders();
    }

    @GetMapping("/{orderId}")
    public OrderDto getSingleOrder(@PathVariable Long orderId){
        return orderService.getOrder(orderId);
    }

    @ExceptionHandler(NoOrderFoundException.class)
    public ResponseEntity<ErrorDto> handleNoOrderException(){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
//                Map.of("error","Cart not found")
                new ErrorDto("Order not found")
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorDto> handleAccessDeniedException(Exception ex){
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                new ErrorDto(ex.getMessage())
        );
    }
}
