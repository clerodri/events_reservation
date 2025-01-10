package com.clerodri.web.controller;

import com.clerodri.core.domain.model.UserModel;
import com.clerodri.core.domain.usecase.user.DeleteUserUseCase;
import com.clerodri.core.domain.usecase.user.RegisterUserUseCase;
import com.clerodri.web.dto.request.RequestLoginDTO;
import com.clerodri.web.dto.request.RequestUserDTO;
import com.clerodri.web.dto.response.ResponseLogin;
import com.clerodri.web.dto.response.ResponseUserDTO;
import com.clerodri.web.mapper.UserWebMapper;
import com.clerodri.web.security.UserDetailServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;
@Slf4j
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Handles user authentication.")
@RequiredArgsConstructor
public class AuthenticationController {

    private final RegisterUserUseCase registerUserUseCase;
    private final DeleteUserUseCase deleteUserUseCase;
    private final UserWebMapper userWebMapper;
    private final UserDetailServiceImpl userDetailService;


    @Operation(
            summary = "Register a new user",
            description = "This endpoint registers a new user by providing a username, password, and email.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User details for registration",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = RequestUserDTO.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Created - ",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseUserDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad Request - ",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    )
            }
    )
    @PostMapping("/register")
    public ResponseEntity<ResponseUserDTO> register(@Valid @RequestBody RequestUserDTO userDTO){
        UserModel userModel = userWebMapper.toDomain(userDTO);
        UserModel userSaved = registerUserUseCase.save(userModel);
        ResponseUserDTO response = userWebMapper.toWeb(userSaved);

        log.info("WEB - AUTH register {}",response);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "User Login",
            description = "This endpoint allows users to log in with their username and password, returning a JWT token upon successful authentication.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User login credentials",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = RequestLoginDTO.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "OK - ",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseLogin.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Not Authorized ",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    )
            }
    )
    @PostMapping("/login")
    public ResponseEntity<ResponseLogin> login(@Valid @RequestBody  RequestLoginDTO loginDTO){
        String token = userDetailService.loginUser(loginDTO);

        log.info("WEB - AUTH login {}",token);
        return  ResponseEntity.ok().body(new ResponseLogin(token));
    }

    @Operation(
            summary = "Delete a user by ID",
            description = "This endpoint deletes a user by their ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "No Content - ",
                            content = @Content(
                                    mediaType = "application/json"
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not Found - ",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden - ",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    )
            }
    )
    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUserById(@PathVariable("userId") Long userId){
        deleteUserUseCase.deleteUser(userId);

        log.info("WEB - AUTH deleteUserById ");
        return ResponseEntity.noContent().build();
    }


}
