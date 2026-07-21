package com.dducwsjvbe.user_service.service.impl;

import com.dducwsjvbe.common_service.advice.custom.KeyCloakException;
import com.dducwsjvbe.user_service.config.JwtRoleUtils;
import com.dducwsjvbe.user_service.dto.identity.*;
import com.dducwsjvbe.user_service.dto.items.RoleRepresentation;
import com.dducwsjvbe.user_service.dto.items.UserResponseDTO;
import com.dducwsjvbe.user_service.dto.request.CreateUserRequest;
import com.dducwsjvbe.user_service.dto.request.LoginRequestDto;
import com.dducwsjvbe.user_service.dto.request.RefreshTokenRequestDto;
import com.dducwsjvbe.user_service.dto.request.RoleAssignmentRequest;
import com.dducwsjvbe.user_service.dto.response.LoginResponse;
import com.dducwsjvbe.user_service.entity.User;
import com.dducwsjvbe.user_service.repository.IdentityClient;
import com.dducwsjvbe.user_service.repository.UserRepository;
import com.dducwsjvbe.user_service.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j(topic = "User-Service-Impl")
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final IdentityClient identityClient;
    private final JwtDecoder jwtDecoder;

    @Value("${idp.client-id}")
    @NonFinal
    private String clientId;

    @Value("${idp.client-secret}")
    @NonFinal
    private String clientSecret;

    @Override
    public UserResponseDTO createUser(CreateUserRequest dto) {
        //get token admin
        var token = identityClient.exchangeClientToken(TokenExchangeParam.builder()
                .grant_type("client_credentials")
                .client_secret(clientSecret)
                .client_id(clientId)
                .scope("openid")
                .build());
        if (token == null || token.getAccessToken()==null) {
            throw new KeyCloakException(
                    "1001",
                    "Token is null",
                    HttpStatus.UNAUTHORIZED
            );
        }

        log.info("Token info={}", token);
            var creationResponse = identityClient.createUser(UserCreationParam.builder()
                    .username(dto.getUsername())
                    .firstName(dto.getFirstName())
                    .lastName(dto.getLastName())
                    .email(dto.getEmail())
                    .enabled(true)
                    .emailVerified(false)
                    .credentials(List.of(Credential.builder()
                            .type("password")
                            .temporary(false)
                            .value(dto.getPassword())
                            .build()))
                    .build(), "Bearer " + token.getAccessToken());
      if (!creationResponse.getStatusCode().is2xxSuccessful()) {
          throw new KeyCloakException(
                  "1002",
                  "Cannot create user in Keycloak",
                  HttpStatus.CONFLICT
          );
      }
        String userId = extractUserId(creationResponse);
        log.info("UserId {}", userId);
        var roles = identityClient.getRoles("Bearer " + token.getAccessToken());
        if (roles.getBody() == null) {
            throw new KeyCloakException(
                    "1003",
                    "Cannot get roles in Keycloak",
                    HttpStatus.NOT_FOUND
            );
        }
        RoleRepresentation roleRepresentation = roles.getBody();
        RoleAssignmentRequest roleAssignmentRequest = new RoleAssignmentRequest(roleRepresentation.getId(), roleRepresentation.getName());
        List<RoleAssignmentRequest> listRole = List.of(roleAssignmentRequest);
        identityClient.addRoles(
                "Bearer " + token.getAccessToken(),
                userId,
                listRole
        );

        User user = new User();
        user.setUserId(userId);
        user.setEmail(dto.getEmail());
        user.setUserName(dto.getUsername());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setDob(dto.getDob());
        user.setName(dto.getFirstName()+" "+dto.getLastName());

        user = userRepository.save(user);
        return toDTO(user);
    }

    @Override
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public UserResponseDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return toDTO(user);
    }

    @Override
    public UserResponseDTO updateUser(Long id, CreateUserRequest dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        user.setEmail(dto.getEmail());
        user.setUserName(dto.getUsername());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setDob(dto.getDob());
        user.setName(dto.getFirstName()+" "+dto.getLastName());

        return toDTO(userRepository.save(user));
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public LoginResponse login(LoginRequestDto request) {
        TokenExchangeResponse token = identityClient.exchangeUserToken(UserTokenExchangeParam.builder()
                .grant_type("password")
                .client_secret(clientSecret)
                .client_id(clientId)
                .scope("openid")
                .username(request.getUsername())
                .password(request.getPassword())
                .build());
        Jwt jwt = jwtDecoder.decode(token.getAccessToken());

        UserInfo userInfo = UserInfo.builder()
                .username(jwt.getClaimAsString("preferred_username"))
                .roles(JwtRoleUtils.extractRoles(jwt))
                .build();
        return LoginResponse.builder()
                .token(token)
                .user(userInfo)
                .build();
    }

    @Override
    public TokenExchangeResponse refresh(RefreshTokenRequestDto request) {
        var token = identityClient.exchangeUserToken(UserTokenExchangeParam.builder()
                .grant_type("refresh_token")
                .client_id(clientId)
                .client_secret(clientSecret)
                .refresh_token(request.getRefreshToken())
                .build());
        return token;
    }

    private UserResponseDTO toDTO(User user) {
        return UserResponseDTO.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .username(user.getUserName())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .dob(user.getDob())
                .name(user.getName())
                .id(user.getId())
                .build();
    }

    //khi create nó trả về header vs key=location nên cần lấy ra để save db
    private String extractUserId(ResponseEntity<?> response) {
        List<String> locations = response.getHeaders().get("Location");
        if (locations == null || locations.isEmpty()) {
            throw new KeyCloakException("1004",
                    "Location header is missing in the response",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
//tách lấy userId ở cuối url
        String location = locations.get(0);
        String[] splitedStr = location.split("/");
        return splitedStr[splitedStr.length - 1];
    }
}
