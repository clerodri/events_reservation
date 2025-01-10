package com.clerodri.core.domain.usecase.user;

import com.clerodri.core.domain.repository.UserRepository;
import com.clerodri.core.exception.UserNotFoundException;

public class DeleteUserUseCaseImpl implements DeleteUserUseCase{

    private final UserRepository userRepository;

    public DeleteUserUseCaseImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void deleteUser(Long userId)  {

        if(!userRepository.existsById(userId)){
            throw new UserNotFoundException("User with ID:"+userId+" not found");

        }
        userRepository.delete(userId);
    }
}
