package com.clerodri.details.repository.jpa;

import com.clerodri.details.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {

   Optional<UserEntity> findByUsername(String username);

   boolean existsByUsernameOrEmail(String username, String email);

}
