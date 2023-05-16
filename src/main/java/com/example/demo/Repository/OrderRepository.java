package com.example.demo.Repository;

import com.example.demo.Entity.Order;
import com.example.demo.Entity.Product;
import com.example.demo.Entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Optional;

@Repository
public interface OrderRepository extends CrudRepository<Order,Integer> {
    public ArrayList<Order> findOrdersByUser(Optional<User> user);

    @Override
    <S extends Order> S save(S entity);

    public ArrayList<Order> findOrderByProductAndUser(Optional<Product>product, Optional<User> user);

    public Order findOrderByProductAndUserAndState(Optional<Product>product, Optional<User> user,String state);

    @Override
    void deleteById(Integer integer);

}
