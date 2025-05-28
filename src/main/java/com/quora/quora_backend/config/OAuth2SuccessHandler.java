package com.quora.quora_backend.config;


import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.quora.quora_backend.Entity.UserEntity;
import com.quora.quora_backend.model.AuthProvider;
import com.quora.quora_backend.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public OAuth2SuccessHandler(JwtTokenProvider tokenProvider, 
                                UserRepository userRepository,
                                PasswordEncoder passwordEncoder) {
        this.tokenProvider = tokenProvider;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                         HttpServletResponse response,
                                         Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String email = (String) attributes.get("email");

        UserEntity user = userRepository.findByEmail(email)
            .orElseGet(() -> {
                UserEntity newUser = new UserEntity();
                newUser.setEmail(email);
                newUser.setUsername(email.split("@")[0]);
                newUser.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
                return userRepository.save(newUser);
            });

      //  String token = tokenProvider.generateToken(user);
        TokenPair token = tokenProvider.generateTokenPair(user);
        // ✅ Log token for debugging
        System.out.println("Generated token: " + token);

        if (!response.isCommitted()) {
            response.setContentType("text/html");
            response.getWriter().write(
                "<html>" +
                    "<head><title>OAuth2 Login Success</title></head>" +
                    "<body>" +
                    "<h2>Login successful!</h2>" +
                    "<p>Your JWT token is:</p>" +
                    "<textarea rows='10' cols='80'>" +"Access token "+ token.getAccessToken() +" "+" " + "</textarea>" +
                    "<br/><br/>" +
                    "<textarea rows='10' cols='80'>" +"Refresh token "+ token.getRefreshToken() +" "+" " + "</textarea>" +
                    "<p>Copy this token to use in API requests.</p>" +
                    "</body>" +
                "</html>"
            );
        }
    }


}


