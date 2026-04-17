package com.example.aiproxyplatform.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuotaStatusResponse {

    private String userId;
    private String plan;
    private int tokensUsed;
    private int tokensRemaining;
    private int requestsUsedInCurrentWindow;
    private int requestsRemainingInCurrentWindow;
    private LocalDateTime rateLimitResetAt;
    private LocalDate quotaResetDate;
}
