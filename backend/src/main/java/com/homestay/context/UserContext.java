package com.homestay.context;

import lombok.Data;

@Data
public class UserContext {
    private Long userId;
    private String username;
    private String userType;
    private String role;
}
