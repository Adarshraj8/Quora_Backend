package com.quora.quora_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.quora.quora_backend.Dto.UserProfileDTO;
import com.quora.quora_backend.service.UserService;


@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserProfileDTO> getUserProfile(@PathVariable Long id) {
        UserProfileDTO profile = userService.getUserProfile(id);
        return ResponseEntity.ok(profile);
    }

    @PostMapping("/{id}/follow")
    public ResponseEntity<Void> followUser(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        // Extract current user ID from token (implementation depends on your JWT setup)
        Long currentUserId = getCurrentUserIdFromToken(token);
        userService.followUser(currentUserId, id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/unfollow")
    public ResponseEntity<Void> unfollowUser(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        Long currentUserId = getCurrentUserIdFromToken(token);
        userService.unfollowUser(currentUserId, id);
        return ResponseEntity.ok().build();
    }

    private Long getCurrentUserIdFromToken(String token) {
        // Implement token parsing to get user ID
        // This should match your JWT implementation
        return 1L; // Placeholder - replace with actual implementation
    }
}