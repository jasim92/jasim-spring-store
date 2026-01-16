package com.jasim.store.repositories;

import com.jasim.store.entities.Category;
import org.springframework.data.repository.CrudRepository;

public interface CategoryRepository extends CrudRepository<Category, Short> {
}