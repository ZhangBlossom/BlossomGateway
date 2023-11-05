package blossom.project.userservice.dao;

import blossom.project.userservice.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserDao extends CrudRepository<User, Long> {
    Optional<User> findByPhoneNumber(String phoneNumber);
}
