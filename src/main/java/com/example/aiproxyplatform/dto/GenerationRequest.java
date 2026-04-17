package com.example.aiproxyplatform.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenerationRequest {

    @NotBlank(message = "userId must not be blank")
    private String userId;

    @NotBlank(message = "prompt must not be blank")
    private String prompt;
}
