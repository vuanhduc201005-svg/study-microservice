package com.dducwsjvbe.user_service.repository;

import com.dducwsjvbe.user_service.dto.identity.TokenExchangeParam;
import com.dducwsjvbe.user_service.dto.identity.TokenExchangeResponse;
import com.dducwsjvbe.user_service.dto.identity.UserCreationParam;
import com.dducwsjvbe.user_service.dto.identity.UserTokenExchangeParam;
import com.dducwsjvbe.user_service.dto.items.RoleRepresentation;
import com.dducwsjvbe.user_service.dto.request.RoleAssignmentRequest;
import feign.QueryMap;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "identity-client", url = "${idp.url}")
@Component
public interface IdentityClient {
    //get token admin
    @PostMapping(
            value = "/realms/dducwsjvbe/protocol/openid-connect/token",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
    )
    TokenExchangeResponse exchangeClientToken(@QueryMap() TokenExchangeParam param);

    //create user
    @PostMapping(
            value = "admin/realms/dducwsjvbe/users",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<?> createUser(@RequestBody() UserCreationParam body, @RequestHeader("authorization") String token);

    //add role to user
    @GetMapping(
            value = "/admin/realms/dducwsjvbe/roles/USER"
    )
    ResponseEntity<RoleRepresentation> getRoles(@RequestHeader("authorization") String token);

    @PostMapping(
            value = "/admin/realms/dducwsjvbe/users/{userId}/role-mappings/realm",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<?> addRoles(@RequestHeader("Authorization") String token,
                               @PathVariable("userId") String userId,
                               @RequestBody List<RoleAssignmentRequest> roles);

    //login
    @PostMapping(
            value = "realms/dducwsjvbe/protocol/openid-connect/token",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
    )
    TokenExchangeResponse exchangeUserToken(@QueryMap() UserTokenExchangeParam param);

}
