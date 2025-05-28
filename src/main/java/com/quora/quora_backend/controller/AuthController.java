package com.quora.quora_backend.controller;


import java.io.IOException;
import java.util.Collections;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.api.client.auth.oauth2.RefreshTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.quora.quora_backend.Dto.GoogleAuthRequest;
import com.quora.quora_backend.Dto.JwtResponse;
import com.quora.quora_backend.Dto.LoginRequest;
import com.quora.quora_backend.Dto.SignUpRequest;
import com.quora.quora_backend.Entity.UserEntity;
import com.quora.quora_backend.Exception.BadRequestException;
import com.quora.quora_backend.Exception.ResourceNotFoundException;
import com.quora.quora_backend.config.JwtTokenProvider;
import com.quora.quora_backend.config.TokenPair;
import com.quora.quora_backend.service.AuthService;
import com.quora.quora_backend.service.UserService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.util.StringUtils;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	 private final AuthService authService;
	    private final UserService userService;
	    private final JwtTokenProvider tokenProvider;

	    public AuthController(AuthService authService, 
	                        UserService userService,
	                        JwtTokenProvider tokenProvider) {
	        this.authService = authService;
	        this.userService = userService;
	        this.tokenProvider = tokenProvider;
	    }


	    @PostMapping("/signin")
	    public ResponseEntity<JwtResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) throws Exception {
	        System.out.println("SIGNIN attempt: " + loginRequest.getUsername());
	        try {
	            JwtResponse response = authService.authenticateUser(loginRequest);
	            return ResponseEntity.ok(response);
	        } catch (Exception e) {
	            System.out.println("Authentication failed: " + e.getMessage());
	            e.printStackTrace();
	            throw e;
	        }
	    }


    @PostMapping("/signup")
    public ResponseEntity<Long> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) throws org.apache.coyote.BadRequestException {
        Long userId = authService.registerUser(signUpRequest);
        return ResponseEntity.ok(userId);
    }
    
    

    @PostMapping("/google")
    public ResponseEntity<JwtResponse> authenticateGoogle(@RequestBody GoogleAuthRequest request) {
        GoogleIdToken.Payload payload = verifyGoogleToken(request.getToken());
        UserEntity user = userService.processGoogleUser(payload);

        // ✅ Generate both access + refresh tokens
        TokenPair tokenPair = tokenProvider.generateTokenPair(user);

        return ResponseEntity.ok(new JwtResponse(
            tokenPair.getAccessToken(),
            tokenPair.getRefreshToken(),
            user.getId(),
            user.getUsername(),
            user.getEmail()
        ));
    }


    @GetMapping("/oauth2/callback/google")
    public ResponseEntity<JwtResponse> handleGoogleCallback(@RequestParam("token") String googleToken) {
        GoogleIdToken.Payload payload = verifyGoogleToken(googleToken);
        UserEntity user = userService.processGoogleUser(payload);
        String token = tokenProvider.generateToken(user);
        return ResponseEntity.ok(new JwtResponse(token, user.getId(), user.getEmail(), user.getUsername()));
    }


    private GoogleIdToken.Payload verifyGoogleToken(String idToken) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(), 
                new GsonFactory())
                .setAudience(Collections.singletonList("943008317917-n7n91jscaa1dtf56og4els3hatmsg4p5.apps.googleusercontent.com"))
                .build();

            GoogleIdToken idTokenObj = verifier.verify(idToken);
            if (idTokenObj != null) {
                return idTokenObj.getPayload();
            }
            throw new BadRequestException("Invalid Google token");
        } catch (Exception e) {
            throw new BadRequestException("Google authentication failed");
        }
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<JwtResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        if (!StringUtils.hasText(refreshToken)) {
            throw new BadRequestException("Refresh token is required");
        }

        if (!tokenProvider.validateToken(refreshToken)) {
            throw new BadRequestException("Invalid refresh token");
        }

        String username = tokenProvider.getUsername(refreshToken);
        UserEntity user = userService.findByUsername(username);

        String newAccessToken = tokenProvider.generateAccessToken(user);
        String newRefreshToken = tokenProvider.generateRefreshToken(user); // rotate if needed

        return ResponseEntity.ok(new JwtResponse(newAccessToken, newRefreshToken, user.getId(), user.getUsername(), user.getEmail()));
    }

}