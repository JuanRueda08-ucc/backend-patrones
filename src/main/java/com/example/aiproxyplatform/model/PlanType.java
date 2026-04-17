package com.example.aiproxyplatform.model;

public enum PlanType {

    FREE(10, 50_000),
    PRO(60, 500_000),
    ENTERPRISE(Integer.MAX_VALUE, Integer.MAX_VALUE);

    private final int requestsPerMinute;
    private final int monthlyTokens;

    PlanType(int requestsPerMinute, int monthlyTokens) {
        this.requestsPerMinute = requestsPerMinute;
        this.monthlyTokens = monthlyTokens;
    }

    public int getRequestsPerMinute() {
        return requestsPerMinute;
    }

    public int getMonthlyTokens() {
        return monthlyTokens;
    }

    public boolean isUnlimited() {
        return this.monthlyTokens == Integer.MAX_VALUE;
    }
}
