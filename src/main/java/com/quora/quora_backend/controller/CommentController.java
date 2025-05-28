package com.quora.quora_backend.controller;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.quora.quora_backend.Dto.CommentDTO;
import com.quora.quora_backend.Dto.CommentInputDTO;
import com.quora.quora_backend.service.CommentService;
import com.quora.quora_backend.utility.TokenUtils;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;
    private final TokenUtils tokenUtils;
    public CommentController(CommentService commentService,TokenUtils tokenUtils) {
        this.commentService = commentService;
        this.tokenUtils=tokenUtils;
    }

    @GetMapping("/answer/{answerId}")
    public ResponseEntity<Page<CommentDTO>> getCommentsByAnswer(
            @PathVariable Long answerId,
            Pageable pageable) {
        Page<CommentDTO> comments = commentService.getCommentsByAnswer(answerId, pageable);
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommentDTO> getCommentById(@PathVariable Long id) {
        CommentDTO comment = commentService.getCommentById(id);
        return ResponseEntity.ok(comment);
    }

    @PostMapping
    public ResponseEntity<CommentDTO> createComment(
            @Valid @RequestBody CommentInputDTO commentInput,
            @RequestHeader("Authorization") String token) {
        Long userId = getCurrentUserIdFromToken(token);
        CommentDTO comment = commentService.createComment(commentInput, userId);
        return ResponseEntity.ok(comment);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommentDTO> updateComment(
            @PathVariable Long id,
            @Valid @RequestBody CommentInputDTO commentInput,
            @RequestHeader("Authorization") String token) {
        Long userId = getCurrentUserIdFromToken(token);
        CommentDTO comment = commentService.updateComment(id, commentInput, userId);
        return ResponseEntity.ok(comment);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        Long userId = getCurrentUserIdFromToken(token);
        commentService.deleteComment(id, userId);
        return ResponseEntity.noContent().build();
    }

    // Update all other methods similarly
    private Long getCurrentUserIdFromToken(String token) {
        return tokenUtils.getUserIdFromToken(token);
    }
}
