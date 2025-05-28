package com.quora.quora_backend.Dto;

import lombok.Data;

@Data
public class UserProfileDTO {
    private Long id;
    private String username;
    private String email;
    private int followersCount;
    private int followingCount;
    private int questionsCount;
    private int answersCount;
}
