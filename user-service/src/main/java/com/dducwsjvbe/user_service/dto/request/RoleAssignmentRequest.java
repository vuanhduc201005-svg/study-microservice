package com.dducwsjvbe.user_service.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoleAssignmentRequest {
    private String id;
    private String name;
}
