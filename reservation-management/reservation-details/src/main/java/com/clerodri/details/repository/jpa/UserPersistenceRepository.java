package com.clerodri.details.repository.jpa;

import com.clerodri.core.domain.model.UserModel;
import com.clerodri.core.domain.repository.UserRepository;
import com.clerodri.details.entity.UserEntity;
import com.clerodri.details.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;


@Repository
@RequiredArgsConstructor
@Profile({"postgresql","in-memory"})
public class UserPersistenceRepository implements UserRepository {

    private final UserJpaRepository userJpaRepository;
    private final UserMapper userMapper;


    @Override
    public UserModel save(UserModel userModel) {
        UserEntity userEntity = userMapper.toEntity(userModel);
        return userMapper.toDomain(userJpaRepository.save(userEntity));
    }

    @Override
    public void delete(Long userId) {
        userJpaRepository.deleteById(userId);
    }


    @Override
    public Boolean existsById(Long userId) {
        return userJpaRepository.existsById(userId);
    }

    @Override
    public Optional<UserModel> findByUsername(String username) {
        return userJpaRepository.findByUsername(username)
                .map(userMapper::toDomain);
    }

    @Override
    public List<UserModel> findAllById(Set<Long> userIds) {
        return userJpaRepository.findAllById(userIds).stream().map(userMapper::toDomain).toList();
    }

    @Override
    public boolean existsByUsernameOrEmail(String username, String email) {
        return userJpaRepository.existsByUsernameOrEmail(username,email);
    }

}
