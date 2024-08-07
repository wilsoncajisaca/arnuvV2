package com.core.arnuv.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum RolEnum {
    ROLE_USER("CLIENTE"),
    ROLE_ADMIN("ADMIN"),
    ROLE_WALKER("PASEADOR");

    private final String displayName;
}