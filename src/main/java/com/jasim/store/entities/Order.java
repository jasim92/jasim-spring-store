package com.jasim.store.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "orders", schema = "store")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;


    @ManyToOne
    @JoinColumn(name = "customer_id")
    private User customer;


    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;


    @Column(name = "total_price")
    private BigDecimal totalPrice;


    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @OneToMany(mappedBy = "order", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private Set<OrderItem> items = new LinkedHashSet<>();


    public Order fromCart(Cart cart, User user){
        var order = new Order();
        order.setTotalPrice(cart.getTotalPrice());
        order.setStatus(PaymentStatus.PENDING);
        order.setCustomer(user);

        cart.getItems().forEach(item -> {
            var orderItem = new OrderItem();
            orderItem.setQuantity(item.getQuantity());
            orderItem.setUnitPrice(item.getProduct().getPrice());
            orderItem.setTotalPrice(item.getTotalPrice());
            orderItem.setProduct(item.getProduct());
            orderItem.setOrder(order);

            order.items.add(orderItem);
        });

        return order;
    }

    public boolean isPlacedBy(User user){
        return customer.equals(user);
    }
}