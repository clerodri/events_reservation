package com.clerodri.core.domain.usecase.user;

import com.clerodri.core.domain.repository.UserRepository;
import com.clerodri.core.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteUserUseCaseImplTest {
    @Mock
    UserRepository userRepository;

    @InjectMocks
    DeleteUserUseCaseImpl deleteUserUseCase;

    @BeforeEach
    void setUp() {
    }

    @Test
    void deleteUserByIdSuccessfully() {

        when(userRepository.existsById(1L)).thenReturn(true);

        doNothing().when(userRepository).delete(1L);

        deleteUserUseCase.deleteUser(1L);

        verify(userRepository,times(1)).delete(1L);
    }

    @Test
    void deleteUserShouldThrowExceptionUserNotFound() {
        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(false);


        UserNotFoundException exception = assertThrows(UserNotFoundException.class,()->{
            deleteUserUseCase.deleteUser(userId);
        });

        assertEquals("User with ID:"+userId+" not found",exception.getMessage());
        verify(userRepository,never()).delete(1L);
    }
}