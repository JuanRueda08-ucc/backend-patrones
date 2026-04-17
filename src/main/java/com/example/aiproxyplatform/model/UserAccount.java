package com.example.aiproxyplatform.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class UserAccount {

    private final String userId;
    private PlanType planType;
    private int requestsUsedInWindow;
    private LocalDateTime rateLimitWindowStart;
    private int monthlyTokensUsed;
    private LocalDate quotaResetDate;
    private final Map<LocalDate, DailyUsageRecord> usageHistory;

    public UserAccount(String userId, PlanType planType) {
        this.userId = userId;
        this.planType = planType;
        this.requestsUsedInWindow = 0;
        this.rateLimitWindowStart = LocalDateTime.now();
        this.monthlyTokensUsed = 0;
        this.quotaResetDate = LocalDate.now().plusMonths(1).withDayOfMonth(1);
        this.usageHistory = new HashMap<>();
    }

    // Returns the record for the given date, creating it if it does not exist yet
    public DailyUsageRecord getOrCreateRecordForDate(LocalDate date) {
        return usageHistory.computeIfAbsent(date, DailyUsageRecord::new);
    }

    // Convenience alias for today's record
    public DailyUsageRecord getOrCreateTodayRecord() {
        return getOrCreateRecordForDate(LocalDate.now());
    }

    // Records token consumption and daily usage for a specific date — called by the quota proxy
    public void recordDailyUsage(LocalDate date, int tokensConsumed) {
        DailyUsageRecord record = getOrCreateRecordForDate(date);
        record.addTokens(tokensConsumed);
        record.addRequest();
        this.monthlyTokensUsed += tokensConsumed;
    }

    // Increments the rate-limit window counter — called by the rate-limit proxy
    public void incrementRequestsInWindow() {
        this.requestsUsedInWindow++;
    }

    public void resetRateLimitWindow() {
        this.requestsUsedInWindow = 0;
        this.rateLimitWindowStart = LocalDateTime.now();
    }

    public void resetMonthlyQuota() {
        this.monthlyTokensUsed = 0;
        this.quotaResetDate = LocalDate.now().plusMonths(1).withDayOfMonth(1);
    }

    public int getRemainingMonthlyTokens() {
        return planType.getMonthlyTokens() - monthlyTokensUsed;
    }

    public int getRemainingRequestsInWindow() {
        return planType.getRequestsPerMinute() - requestsUsedInWindow;
    }

    public boolean hasMonthlyTokensAvailable(int tokensRequired) {
        return getRemainingMonthlyTokens() >= tokensRequired;
    }

    public boolean hasRequestsAvailableInWindow() {
        return requestsUsedInWindow < planType.getRequestsPerMinute();
    }

    // --- Getters ---

    public String getUserId() {
        return userId;
    }

    public PlanType getPlanType() {
        return planType;
    }

    public int getRequestsUsedInWindow() {
        return requestsUsedInWindow;
    }

    public LocalDateTime getRateLimitWindowStart() {
        return rateLimitWindowStart;
    }

    public int getMonthlyTokensUsed() {
        return monthlyTokensUsed;
    }

    public LocalDate getQuotaResetDate() {
        return quotaResetDate;
    }

    public Map<LocalDate, DailyUsageRecord> getUsageHistory() {
        return Collections.unmodifiableMap(usageHistory);
    }

    // --- Setter for plan upgrade ---

    public void setPlanType(PlanType planType) {
        this.planType = planType;
    }
}
