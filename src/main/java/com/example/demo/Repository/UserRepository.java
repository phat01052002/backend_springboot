package com.example.demo.Repository;

import com.example.demo.Entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User,Integer> {
     public User findUserByUsernameAndPassword(String user, String pass);

     @Override
     <S extends User> S save(S entity);
}
