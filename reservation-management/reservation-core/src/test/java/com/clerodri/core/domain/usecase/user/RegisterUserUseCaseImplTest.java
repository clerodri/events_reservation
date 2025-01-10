package com.clerodri.core.domain.usecase.user;

import com.clerodri.core.domain.model.RolEnum;
import com.clerodri.core.domain.model.UserModel;
import com.clerodri.core.domain.repository.UserRepository;
import com.clerodri.core.domain.service.PasswordService;
import com.clerodri.core.exception.UserDuplicatedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.Extensions;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegisterUserUseCaseImplTest {

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordService passwordService;

    @InjectMocks
    RegisterUserUseCaseImpl registerUserUseCase;

    @BeforeEach
    void init() {
    }

    @Test
    void registerUserShouldSaveUserSuccessfully() {
        UserModel userModel = new UserModel(
                null,
                "rorotest",
                "1234",
                "rorotest@gmail.com",
             null
        );
        UserModel userExpected = new UserModel(
                1L,
                "rorotest",
                "1234",
                "rorotest@gmail.com",
                RolEnum.USER
        );

        when(registerUserUseCase.save(userModel)).thenReturn(userExpected);

        UserModel savedUser = registerUserUseCase.save(userModel);

        assertNotNull(savedUser.getId());
        assertEquals("rorotest", savedUser.getUsername());
        assertEquals("1234", savedUser.getPassword());
        assertEquals("rorotest@gmail.com", savedUser.getEmail());
        assertEquals(RolEnum.USER, savedUser.getRole());
    }



    @Test
    void registerUserShouldThrowExceptionForUsernameDuplicated(){
        UserModel userModel = new UserModel(
                1L,
                "rorotest",
                "1234",
                "rorotest@gmail.com",
                RolEnum.USER
        );
        when(userRepository.existsByUsernameOrEmail(userModel.getUsername(),userModel.getEmail())).thenReturn(true);

        UserDuplicatedException exception = assertThrows(UserDuplicatedException.class, () -> {
            registerUserUseCase.save(userModel);
        });

        assertEquals("Username:"+userModel.getUsername()+" or Email:"+userModel.getEmail()+" already exists",exception.getMessage());


    }

}