package com.clerodri.core.domain.repository;

import com.clerodri.core.domain.model.UserModel;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserRepository {

    UserModel save(UserModel userModel);
    void delete(Long userId);
    Boolean existsById(Long userId);
    Optional<UserModel> findByUsername(String username);
    List<UserModel> findAllById(Set<Long> userIds);
    boolean existsByUsernameOrEmail(String username, String email);
}
