package com.quora.quora_backend.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class QuestionDTO {
    private Long id;
    
    @NotBlank
    @Size(max = 500)
    private String title;
    
    @Size(max = 5000)
    private String description;
    
    private UserSummaryDTO user;
    private int answerCount;
    private int likeCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
