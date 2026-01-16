package com.jasim.store.repositories;

import com.jasim.store.dtos.OrderDto;
import com.jasim.store.entities.Order;
import com.jasim.store.entities.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @EntityGraph(attributePaths = "items.product")
    @Query("Select o from Order o Where o.customer = :customer")
    List<Order> getAllByCustomer(@Param("customer") User customer);

    @EntityGraph(attributePaths = "items.product")
    @Query("select o from Order o where o.id = :orderId")
    Optional<Order> findOrderByItems(@Param("orderId") Long orderId);


}