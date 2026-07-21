package com.dducwsjvbe.user_service.service.interfaces;

import com.dducwsjvbe.user_service.dto.identity.TokenExchangeResponse;
import com.dducwsjvbe.user_service.dto.items.UserResponseDTO;
import com.dducwsjvbe.user_service.dto.request.CreateUserRequest;
import com.dducwsjvbe.user_service.dto.request.LoginRequestDto;
import com.dducwsjvbe.user_service.dto.request.RefreshTokenRequestDto;
import com.dducwsjvbe.user_service.dto.response.LoginResponse;

import java.util.List;

public interface UserService {
    UserResponseDTO createUser(CreateUserRequest dto);
    List<UserResponseDTO> getAllUsers();
    UserResponseDTO getUserById(Long id);
    UserResponseDTO updateUser(Long id, CreateUserRequest dto);
    void deleteUser(Long id);
    LoginResponse login(LoginRequestDto request);
    TokenExchangeResponse refresh(RefreshTokenRequestDto request);

}
