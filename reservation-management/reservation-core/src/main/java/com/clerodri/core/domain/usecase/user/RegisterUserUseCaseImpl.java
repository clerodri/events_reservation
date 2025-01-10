package com.clerodri.core.domain.usecase.user;


import com.clerodri.core.domain.model.RolEnum;
import com.clerodri.core.domain.model.UserModel;
import com.clerodri.core.domain.repository.UserRepository;
import com.clerodri.core.domain.service.PasswordService;
import com.clerodri.core.exception.UserDuplicatedException;


public class RegisterUserUseCaseImpl implements RegisterUserUseCase {

    private final UserRepository userRepository;
    private final PasswordService passwordService;



    public RegisterUserUseCaseImpl(UserRepository userRepository, PasswordService passwordService) {
        this.userRepository = userRepository;
        this.passwordService = passwordService;
    }

    @Override
    public UserModel save(UserModel userModel) {

        boolean isUserRegistered = userRepository.existsByUsernameOrEmail(userModel.getUsername(),
                                                                            userModel.getEmail());
        if (isUserRegistered){
            throw new UserDuplicatedException("Username:"+userModel.getUsername()+" or Email:"+userModel.getEmail()+" already exists");
        }

        String encodePassword = passwordService.encode(userModel.getPassword());
        userModel.setPassword(encodePassword);
        userModel.setRole(RolEnum.USER);
        return userRepository.save(userModel);
    }


}
