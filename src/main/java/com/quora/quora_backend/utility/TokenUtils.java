package com.quora.quora_backend.utility;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.quora.quora_backend.config.JwtTokenProvider;

@Component
public class TokenUtils {

    private final JwtTokenProvider tokenProvider;

    public TokenUtils(JwtTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    public Long getUserIdFromToken(String authHeader) {
        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (tokenProvider.validateToken(token)) {
                return Long.parseLong(tokenProvider.getUsername(token));
            }
        }
        throw new IllegalArgumentException("Invalid or missing JWT token");
    }
}
