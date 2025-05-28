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

import com.quora.quora_backend.Dto.QuestionDTO;
import com.quora.quora_backend.Dto.QuestionInputDTO;
import com.quora.quora_backend.service.QuestionService;
import com.quora.quora_backend.utility.TokenUtils;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    private final QuestionService questionService;
    private final TokenUtils tokenUtils;
    
    public QuestionController(QuestionService questionService,TokenUtils tokenUtils) {
        this.questionService = questionService;
        this.tokenUtils=tokenUtils;
    }

    @GetMapping
    public ResponseEntity<Page<QuestionDTO>> getAllQuestions(Pageable pageable) {
        Page<QuestionDTO> questions = questionService.getAllQuestions(pageable);
        return ResponseEntity.ok(questions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuestionDTO> getQuestionById(@PathVariable Long id) {
        QuestionDTO question = questionService.getQuestionById(id);
        return ResponseEntity.ok(question);
    }

    @PutMapping("/{id}")
    public ResponseEntity<QuestionDTO> updateQuestion(
            @PathVariable Long id,
            @Valid @RequestBody QuestionInputDTO questionInput,
            @RequestHeader("Authorization") String token) {
        Long userId = getCurrentUserIdFromToken(token);
        QuestionDTO question = questionService.updateQuestion(id, questionInput, userId);
        return ResponseEntity.ok(question);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestion(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        Long userId = getCurrentUserIdFromToken(token);
        questionService.deleteQuestion(id, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<Void> likeQuestion(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        Long userId = getCurrentUserIdFromToken(token);
        questionService.likeQuestion(id, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/unlike")
    public ResponseEntity<Void> unlikeQuestion(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        Long userId = getCurrentUserIdFromToken(token);
        questionService.unlikeQuestion(id, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping
    public ResponseEntity<QuestionDTO> createQuestion(
            @Valid @RequestBody QuestionInputDTO questionInput,
            @RequestHeader("Authorization") String token) {
        Long userId = tokenUtils.getUserIdFromToken(token);
        QuestionDTO question = questionService.createQuestion(questionInput, userId);
        return ResponseEntity.ok(question);
    }

    // Update all other methods similarly
    private Long getCurrentUserIdFromToken(String token) {
        return tokenUtils.getUserIdFromToken(token);
    }
}