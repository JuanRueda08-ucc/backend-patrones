package com.example.aiproxyplatform.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpgradePlanResponse {

    private String message;
    private String userId;
    private String oldPlan;
    private String newPlan;
}
