package com.yuosef.e_commerce.repository;

import com.yuosef.e_commerce.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);
}