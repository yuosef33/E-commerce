package com.yuosef.e_commerce.repository;

import com.yuosef.e_commerce.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByName(String name);
    Optional<Category> findBySlug(String slug);
}