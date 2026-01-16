package com.jasim.store.repositories;

import com.jasim.store.dtos.ProductDto;
import com.jasim.store.entities.Product;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {


    @EntityGraph(attributePaths = "category")
    List<Product> findProductByCategory_Id(Short categoryId);

    @EntityGraph(attributePaths = "category")
    @Query("SELECT p FROM Product p")
    List<Product> findProductsByCategory();
}