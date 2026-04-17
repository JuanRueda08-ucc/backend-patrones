package com.example.aiproxyplatform.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenerationResponse {

    private String userId;
    private String prompt;
    private String generatedText;
    private int tokensConsumed;
    private String plan;
    private int remainingRequestsInWindow;
    private int remainingMonthlyTokens;
    private LocalDateTime timestamp;
}
