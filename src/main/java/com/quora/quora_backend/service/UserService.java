package com.quora.quora_backend.service;


import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.quora.quora_backend.Dto.UserProfileDTO;
import com.quora.quora_backend.Dto.UserSummaryDTO;
import com.quora.quora_backend.Entity.UserEntity;
import com.quora.quora_backend.Exception.ResourceNotFoundException;
import com.quora.quora_backend.repository.UserRepository;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.quora.quora_backend.Entity.UserEntity;
import com.quora.quora_backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    public UserService(UserRepository userRepository,PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        
    }

    public UserProfileDTO getUserProfile(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        UserProfileDTO profile = new UserProfileDTO();
        profile.setId(user.getId());
        profile.setUsername(user.getUsername());
        profile.setEmail(user.getEmail());
        profile.setFollowersCount(user.getFollowers().size());
        profile.setFollowingCount(user.getFollowing().size());
        profile.setQuestionsCount(user.getQuestions().size());
        profile.setAnswersCount(user.getAnswers().size());

        return profile;
    }

    @Transactional
    public void followUser(Long followerId, Long followingId) {
        if (followerId.equals(followingId)) {
            throw new IllegalArgumentException("You cannot follow yourself");
        }

        UserEntity follower = userRepository.findById(followerId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", followerId));
        
        UserEntity following = userRepository.findById(followingId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", followingId));

        if (follower.getFollowing().contains(following)) {
            throw new IllegalArgumentException("You are already following this user");
        }

        follower.getFollowing().add(following);
        following.getFollowers().add(follower);
        
        userRepository.save(follower);
        userRepository.save(following);
    }

    @Transactional
    public void unfollowUser(Long followerId, Long followingId) {
        if (followerId.equals(followingId)) {
            throw new IllegalArgumentException("You cannot unfollow yourself");
        }

        UserEntity follower = userRepository.findById(followerId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", followerId));
        
        UserEntity following = userRepository.findById(followingId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", followingId));

        if (!follower.getFollowing().contains(following)) {
            throw new IllegalArgumentException("You are not following this user");
        }

        follower.getFollowing().remove(following);
        following.getFollowers().remove(follower);
        
        userRepository.save(follower);
        userRepository.save(following);
    }

    public UserSummaryDTO getUserSummary(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        UserSummaryDTO summary = new UserSummaryDTO();
        summary.setId(user.getId());
        summary.setUsername(user.getUsername());
        
        return summary;
    }
    
    public UserEntity processGoogleUser(Payload payload) {
        String email = payload.getEmail();
        String name = (String) payload.get("name");
        
        return userRepository.findByEmail(email)
            .orElseGet(() -> {
                UserEntity newUser = new UserEntity();
                newUser.setEmail(email);
                newUser.setUsername(email.split("@")[0]); // Use email prefix as username
                newUser.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
                // Set other fields from Google payload as needed
                return userRepository.save(newUser);
            });
    }
    
    public UserEntity findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
    }

}