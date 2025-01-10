package com.clerodri.web.controller;

import com.clerodri.core.domain.model.RolEnum;
import com.clerodri.core.domain.model.UserModel;
import com.clerodri.core.domain.usecase.user.DeleteUserUseCase;
import com.clerodri.core.domain.usecase.user.RegisterUserUseCase;
import com.clerodri.core.exception.UserDuplicatedException;
import com.clerodri.web.dto.request.RequestLoginDTO;
import com.clerodri.web.dto.request.RequestUserDTO;
import com.clerodri.web.exception.DomainExceptionHandler;
import com.clerodri.web.exception.ErrorResponse;
import com.clerodri.core.exception.NotAuthorizationException;
import com.clerodri.web.mapper.UserWebMapper;
import com.clerodri.web.security.UserDetailServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
public class AuthenticationControllerTest {

    MockMvc mockMvc;

    DeleteUserUseCase deleteUserUseCase=mock(DeleteUserUseCase .class);
     RegisterUserUseCase registerUserUseCase= mock(RegisterUserUseCase .class);
     UserWebMapper userWebMapper = mock(UserWebMapper.class);
     UserDetailServiceImpl userDetailService = mock(UserDetailServiceImpl .class);;

    AuthenticationController subject;

    RequestUserDTO requestUserDTO;

    ObjectMapper objectMapper;

    @BeforeEach
    public void init(){
        subject = new AuthenticationController(
                registerUserUseCase,
                deleteUserUseCase,
                userWebMapper,
                userDetailService
        );


        mockMvc = MockMvcBuilders.standaloneSetup(subject)
                .setControllerAdvice(new DomainExceptionHandler())
                .build();

        objectMapper = new ObjectMapper()
                .registerModule(new ParameterNamesModule())
                .registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule());
    }

    /*  TEST REGISTER USER  */
    @Test
    void AuthenticationController_Register_WhenSuccess() throws Exception {
        requestUserDTO = new RequestUserDTO(
                "roro",
                "1234",
                "roro@gmail.com"
        );


        String requestBody = objectMapper.writeValueAsString(requestUserDTO);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isCreated());
    }
    @Test
    void AuthenticationController_Register_WhenUsernameOrEmailAlreadyUsed() throws Exception {
        requestUserDTO = new RequestUserDTO(
                "roro",
                "1234","roro@gmail.com"
        );
        UserModel userModel = new UserModel(
            null,
                requestUserDTO.username(),
                requestUserDTO.password(),
                requestUserDTO.email(),
                RolEnum.USER
        );

        ErrorResponse expected = new ErrorResponse(
                null,
                HttpStatus.BAD_REQUEST.value(),
                "Error, User duplicated",
                "Username:"+requestUserDTO.username()+" or Email:"+requestUserDTO.email()+" already exists",
                null
        );

        when(userWebMapper.toDomain(requestUserDTO)).thenReturn(userModel);
        when(registerUserUseCase.save(userModel))
                .thenThrow(new UserDuplicatedException(
                        "Username:"+requestUserDTO.username()+" or Email:"+requestUserDTO.email()+" already exists"));

        String requestBody = objectMapper.writeValueAsString(requestUserDTO);

        MvcResult mvcResult = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isBadRequest()).andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();

        ErrorResponse errorResponse = objectMapper.readValue(contentAsString, ErrorResponse.class);
        assertThat(errorResponse.getError()).isEqualTo(expected.getError());
        assertThat(errorResponse.getMessage()).isEqualTo(expected.getMessage());
        assertThat(errorResponse.getStatus()).isEqualTo(expected.getStatus());
    }

    /*  TEST LOGIN USER  */
    @Test
    void AuthenticationController_Login_WhenSuccess() throws Exception {
        RequestLoginDTO requestLoginDTO = new RequestLoginDTO(
                "roro",
                "1234"
        );
        String token="token1234";

        when(userDetailService.loginUser(requestLoginDTO)).thenReturn(token);

        String requestBody = objectMapper.writeValueAsString(requestLoginDTO);
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token1234"));
    }
    @Test
    void AuthenticationController_Login_WhenCredentialsIncorrect() throws Exception {
        RequestLoginDTO requestLoginDTO = new RequestLoginDTO(
                "roro","invalidapassword"
        );
        ErrorResponse expected = new ErrorResponse(
                null,
                HttpStatus.UNAUTHORIZED.value(),
                "Error, Unauthorized",
                "User:"+requestLoginDTO.username()+" is not Authorization",
                null
        );


        when(userDetailService.loginUser(requestLoginDTO))
                .thenThrow(new NotAuthorizationException("User:"+requestLoginDTO.username()+" is not Authorization"));

        String requestBody = objectMapper.writeValueAsString(requestLoginDTO);
        MvcResult mvcResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isUnauthorized()).andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();

        ErrorResponse errorResponse = objectMapper.readValue(contentAsString, ErrorResponse.class);
        assertThat(errorResponse.getError()).isEqualTo(expected.getError());
        assertThat(errorResponse.getMessage()).isEqualTo(expected.getMessage());
        assertThat(errorResponse.getStatus()).isEqualTo(expected.getStatus());

        verify(userDetailService,times(1)).loginUser(requestLoginDTO);
    }

}