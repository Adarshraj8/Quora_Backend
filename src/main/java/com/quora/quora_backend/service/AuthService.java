package com.quora.quora_backend.service;

import org.apache.coyote.BadRequestException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import com.quora.quora_backend.Dto.JwtResponse;
import com.quora.quora_backend.Dto.LoginRequest;
import com.quora.quora_backend.Dto.SignUpRequest;
import com.quora.quora_backend.Entity.UserEntity;
import com.quora.quora_backend.Exception.AppException;
import com.quora.quora_backend.config.JwtTokenProvider;
import com.quora.quora_backend.config.TokenPair;
import com.quora.quora_backend.model.AuthProvider;
import com.quora.quora_backend.repository.UserRepository;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    public AuthService(AuthenticationManager authenticationManager, 
                      UserRepository userRepository, 
                      PasswordEncoder passwordEncoder, 
                      JwtTokenProvider tokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    public JwtResponse authenticateUser(LoginRequest loginRequest) throws BadRequestException {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(),
                loginRequest.getPassword()
            )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserEntity user = userRepository.findByUsernameOrEmail(
                loginRequest.getUsername(), 
                loginRequest.getUsername())
            .orElseThrow(() -> new BadRequestException("User not found"));

        // Generate both access and refresh tokens
        TokenPair tokenPair = tokenProvider.generateTokenPair(user);

        // Return both tokens inside JwtResponse
        return new JwtResponse(
            tokenPair.getAccessToken(),
            tokenPair.getRefreshToken(),
            user.getId(),
            user.getUsername(),
            user.getEmail()
        );
    }


    public Long registerUser(SignUpRequest signUpRequest) throws BadRequestException {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            throw new BadRequestException("Username is already taken");
        }
        
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new BadRequestException("Email is already in use");
        }
        
        UserEntity user = new UserEntity();
        user.setUsername(signUpRequest.getUsername());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        user.setProvider(AuthProvider.LOCAL);
        
        return userRepository.save(user).getId();
    }
}