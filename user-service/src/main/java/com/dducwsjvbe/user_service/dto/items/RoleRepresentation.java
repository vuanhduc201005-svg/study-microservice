package com.dducwsjvbe.user_service.dto.items;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoleRepresentation {
    private String id;
    private String name;
    private String description;
    private boolean composite;
    private boolean clientRole;
    private String containerId;
}
