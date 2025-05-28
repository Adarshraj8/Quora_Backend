package com.quora.quora_backend.Dto;


import lombok.Data;

@Data
public class JwtResponse {
    private String accessToken;
    private String refreshToken;
    private String type = "Bearer";
    private Long id;
    private String username;
    private String email;

 // Access + Refresh Token constructor
    public JwtResponse(String accessToken, String refreshToken, Long id, String username, String email) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.id = id;
        this.username = username;
        this.email = email;
    }

    // Access Token only constructor (for Google login etc.)
    public JwtResponse(String accessToken, Long id, String username, String email) {
        this.accessToken = accessToken;
        this.id = id;
        this.username = username;
        this.email = email;
    }
}
