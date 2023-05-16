package com.example.demo.Repository;

import com.example.demo.Entity.Category;
import com.example.demo.Entity.Product;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends CrudRepository<Product,Integer> {
    @Override
    Optional<Product> findById(Integer integer);

    public Iterable<Product> findProductByCategory(Optional<Category> cate);

    public Iterable<Product> findProductByNameContaining(String name);
}
