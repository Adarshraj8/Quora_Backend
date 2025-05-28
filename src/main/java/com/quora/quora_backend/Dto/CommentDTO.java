package com.quora.quora_backend.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDTO {
    private Long id;
    
    @NotBlank
    @Size(max = 2000)
    private String content;
    
    private UserSummaryDTO user;
    private AnswerSummaryDTO answer;
    private CommentSummaryDTO parentComment;
    private int replyCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

