package com.example.aiproxyplatform.service;

import com.example.aiproxyplatform.dto.QuotaHistoryItemResponse;
import com.example.aiproxyplatform.dto.QuotaStatusResponse;
import com.example.aiproxyplatform.dto.UpgradePlanRequest;
import com.example.aiproxyplatform.dto.UpgradePlanResponse;
import com.example.aiproxyplatform.exception.UserNotFoundException;
import com.example.aiproxyplatform.model.DailyUsageRecord;
import com.example.aiproxyplatform.model.PlanType;
import com.example.aiproxyplatform.model.UserAccount;
import com.example.aiproxyplatform.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuotaManagementService {

    private static final Logger log = LoggerFactory.getLogger(QuotaManagementService.class);
    private static final int HISTORY_MAX_DAYS = 7;

    private final UserRepository userRepository;

    public QuotaManagementService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public QuotaStatusResponse getStatus(String userId) {
        UserAccount user = getUserOrThrow(userId);

        boolean unlimited = user.getPlanType().isUnlimited();
        int tokensRemaining       = unlimited ? -1 : user.getRemainingMonthlyTokens();
        int requestsRemaining     = unlimited ? -1 : user.getRemainingRequestsInWindow();

        return QuotaStatusResponse.builder()
                .userId(user.getUserId())
                .plan(user.getPlanType().name())
                .tokensUsed(user.getMonthlyTokensUsed())
                .tokensRemaining(tokensRemaining)
                .requestsUsedInCurrentWindow(user.getRequestsUsedInWindow())
                .requestsRemainingInCurrentWindow(requestsRemaining)
                .rateLimitResetAt(user.getRateLimitWindowStart().plusMinutes(1))
                .quotaResetDate(user.getQuotaResetDate())
                .build();
    }

    public List<QuotaHistoryItemResponse> getHistory(String userId) {
        UserAccount user = getUserOrThrow(userId);

        return user.getUsageHistory().values().stream()
                .sorted(Comparator.comparing(DailyUsageRecord::getDate).reversed())
                .limit(HISTORY_MAX_DAYS)
                .sorted(Comparator.comparing(DailyUsageRecord::getDate))
                .map(this::toHistoryItemResponse)
                .collect(Collectors.toList());
    }

    public UpgradePlanResponse upgradePlan(UpgradePlanRequest request) {
        UserAccount user = getUserOrThrow(request.getUserId());

        PlanType oldPlan = user.getPlanType();
        PlanType newPlan = parsePlanType(request.getNewPlan());

        user.setPlanType(newPlan);
        userRepository.save(user);

        log.info("Plan upgraded — user: '{}', {} -> {}.", user.getUserId(), oldPlan.name(), newPlan.name());

        return UpgradePlanResponse.builder()
                .message("Plan upgraded successfully.")
                .userId(user.getUserId())
                .oldPlan(oldPlan.name())
                .newPlan(newPlan.name())
                .build();
    }

    private UserAccount getUserOrThrow(String userId) {
        return userRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    private PlanType parsePlanType(String planName) {
        try {
            return PlanType.valueOf(planName.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Invalid plan: '" + planName + "'. Valid values are: FREE, PRO, ENTERPRISE."
            );
        }
    }

    private QuotaHistoryItemResponse toHistoryItemResponse(DailyUsageRecord record) {
        return QuotaHistoryItemResponse.builder()
                .date(record.getDate())
                .tokensConsumed(record.getTokensConsumed())
                .requestsCount(record.getRequestsCount())
                .build();
    }
}
