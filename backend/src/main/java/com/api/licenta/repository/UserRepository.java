package com.api.licenta.repository;

import com.api.licenta.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findAll();

    User save(User user);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query(value = "SELECT password FROM user WHERE email = :email", nativeQuery = true)
    String findPasswordByEmail(@Param("email") String email);
}
