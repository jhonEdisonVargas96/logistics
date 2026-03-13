package com.jhon.logistics.domain.model.core;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long      id;
    private String    username;
    private String    email;
    private String    passwordHash;
    private String    role;
    private String    status;
    private LocalDateTime createdAt;
}