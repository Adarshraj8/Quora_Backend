package com.quora.quora_backend.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class CommentInputDTO {
    @NotBlank
    @Size(max = 2000)
    private String content;
    
    private Long answerId;
    private Long parentCommentId;
}
