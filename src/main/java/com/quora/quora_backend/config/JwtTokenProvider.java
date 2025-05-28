package com.quora.quora_backend.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import com.quora.quora_backend.Entity.UserEntity;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${app.jwt-secret}")
    private String jwtSecret;

    @Value("${app.jwt-expiration-milliseconds}")
    private long jwtExpirationDate;

    @Value("${app.jwt-refresh-expiration-ms}")
    private long refreshTokenExpirationMs;

    // 🔐 Generate Access + Refresh token pair
    public TokenPair generateTokenPair(UserEntity user) {
        String accessToken = generateAccessToken(user);
        String refreshToken = generateRefreshToken(user);
        return new TokenPair(accessToken, refreshToken);
    }

    public String generateAccessToken(UserEntity user) {
        return buildToken(user, jwtExpirationDate);
    }

    public String generateRefreshToken(UserEntity user) {
        return buildToken(user, refreshTokenExpirationMs);
    }
    
    private String buildToken(UserEntity user, long expiration) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("userId", user.getId())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key())
                .compact();
    }

    // 🔐 Existing method: for OAuth2User
    public String generateToken(OAuth2User oauthUser) {
        String username = oauthUser.getName();

        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + jwtExpirationDate);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(currentDate)
                .setExpiration(expireDate)
                .signWith(key())
                .compact();
    }

    // 🔐 Existing method: for UserEntity
    public String generateToken(UserEntity user) {
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + jwtExpirationDate);

        return Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuedAt(currentDate)
                .setExpiration(expireDate)
                .signWith(key())
                .compact();
    }

    // 🔍 Validate token
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key())
                    .build()
                    .parse(token);
            return true;
        } catch (MalformedJwtException ex) {
            System.out.println("Malformed JWT: " + ex.getMessage());
            throw new RuntimeException("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            System.out.println("Expired JWT: " + ex.getMessage());
            throw new RuntimeException("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            System.out.println("Unsupported JWT: " + ex.getMessage());
            throw new RuntimeException("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            System.out.println("Illegal argument JWT: " + ex.getMessage());
            throw new RuntimeException("JWT claims string is empty");
        }
    }


    // 🔍 Extract username
    public String getUsername(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    // 🔐 Secret key decoding
    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }
}
