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

import com.quora.quora_backend.Dto.AnswerDTO;
import com.quora.quora_backend.Dto.AnswerInputDTO;
import com.quora.quora_backend.service.AnswerService;
import com.quora.quora_backend.utility.TokenUtils;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/answers")
public class AnswerController {

    private final AnswerService answerService;
    private final TokenUtils tokenUtils;
    public AnswerController(AnswerService answerService,TokenUtils tokenUtils) {
        this.answerService = answerService;
        this.tokenUtils=tokenUtils;
    }

    @GetMapping("/question/{questionId}")
    public ResponseEntity<Page<AnswerDTO>> getAnswersByQuestion(
            @PathVariable Long questionId,
            Pageable pageable) {
        Page<AnswerDTO> answers = answerService.getAnswersByQuestion(questionId, pageable);
        return ResponseEntity.ok(answers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnswerDTO> getAnswerById(@PathVariable Long id) {
        AnswerDTO answer = answerService.getAnswerById(id);
        return ResponseEntity.ok(answer);
    }

    @PostMapping
    public ResponseEntity<AnswerDTO> createAnswer(
            @Valid @RequestBody AnswerInputDTO answerInput,
            @RequestHeader("Authorization") String token) {
        Long userId = getCurrentUserIdFromToken(token);
        AnswerDTO answer = answerService.createAnswer(answerInput, userId);
        return ResponseEntity.ok(answer);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AnswerDTO> updateAnswer(
            @PathVariable Long id,
            @Valid @RequestBody AnswerInputDTO answerInput,
            @RequestHeader("Authorization") String token) {
        Long userId = getCurrentUserIdFromToken(token);
        AnswerDTO answer = answerService.updateAnswer(id, answerInput, userId);
        return ResponseEntity.ok(answer);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAnswer(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        Long userId = getCurrentUserIdFromToken(token);
        answerService.deleteAnswer(id, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<Void> likeAnswer(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        Long userId = getCurrentUserIdFromToken(token);
        answerService.likeAnswer(id, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/unlike")
    public ResponseEntity<Void> unlikeAnswer(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        Long userId = getCurrentUserIdFromToken(token);
        answerService.unlikeAnswer(id, userId);
        return ResponseEntity.ok().build();
    }

    // Update all other methods similarly
    private Long getCurrentUserIdFromToken(String token) {
        return tokenUtils.getUserIdFromToken(token);
    }
}
