package com.dducwsjvbe.user_service.dto.identity;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationParam {
    String username;
    boolean enabled;
    String email;
    boolean emailVerified;
    String firstName;
    String lastName;

    List<Credential> credentials;
}
/*
body create user
 */