package com.example.aiproxyplatform.controller;

import com.example.aiproxyplatform.dto.QuotaHistoryItemResponse;
import com.example.aiproxyplatform.dto.QuotaStatusResponse;
import com.example.aiproxyplatform.dto.UpgradePlanRequest;
import com.example.aiproxyplatform.dto.UpgradePlanResponse;
import com.example.aiproxyplatform.service.QuotaManagementService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/quota")
public class QuotaController {

    private final QuotaManagementService quotaManagementService;

    public QuotaController(QuotaManagementService quotaManagementService) {
        this.quotaManagementService = quotaManagementService;
    }

    @GetMapping("/status")
    public ResponseEntity<QuotaStatusResponse> getStatus(@RequestParam String userId) {
        return ResponseEntity.ok(quotaManagementService.getStatus(userId));
    }

    @GetMapping("/history")
    public ResponseEntity<List<QuotaHistoryItemResponse>> getHistory(@RequestParam String userId) {
        return ResponseEntity.ok(quotaManagementService.getHistory(userId));
    }

    @PostMapping("/upgrade")
    public ResponseEntity<UpgradePlanResponse> upgradePlan(@RequestBody @Valid UpgradePlanRequest request) {
        return ResponseEntity.ok(quotaManagementService.upgradePlan(request));
    }
}
