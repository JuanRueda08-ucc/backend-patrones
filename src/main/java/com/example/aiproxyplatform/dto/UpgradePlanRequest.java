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
public class UpgradePlanRequest {

    @NotBlank(message = "userId must not be blank")
    private String userId;

    @NotBlank(message = "newPlan must not be blank")
    private String newPlan;
}
