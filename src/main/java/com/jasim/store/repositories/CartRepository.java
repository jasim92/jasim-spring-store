package com.jasim.store.repositories;

import com.jasim.store.entities.Cart;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface CartRepository extends JpaRepository<Cart, UUID> {

    //this query is created to optimization coz it get all the required details in single queries
    //before this we used default JPA method findById
    @EntityGraph(attributePaths = "items.product")
    @Query("Select c From Cart c Where c.id = :cart_id")
    Optional<Cart> findCartById(@Param("cart_id") UUID cart_id);
}