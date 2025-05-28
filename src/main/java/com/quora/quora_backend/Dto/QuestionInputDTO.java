package com.quora.quora_backend.Dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class QuestionInputDTO {
    @NotBlank
    @Size(max = 500)
    private String title;
    
    @Size(max = 5000)
    private String description;
}
