package com.example.demo.Repository;

import com.example.demo.Entity.Category;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Optional;

@Repository
public interface CategoryRepository extends CrudRepository<Category,Integer> {
    Optional<Category> findById(Integer integer);
}
