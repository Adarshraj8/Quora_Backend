package com.quora.quora_backend.Dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
@Data
public class AnswerInputDTO {
    @NotBlank
    @Size(max = 10000)
    private String content;
    
    private Long questionId;
}
