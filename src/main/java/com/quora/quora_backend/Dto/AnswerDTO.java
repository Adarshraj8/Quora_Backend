package com.quora.quora_backend.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AnswerDTO {
    private Long id;
    
    @NotBlank
    @Size(max = 10000)
    private String content;
    
    private UserSummaryDTO user;
    private QuestionSummaryDTO question;
    private int commentCount;
    private int likeCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}