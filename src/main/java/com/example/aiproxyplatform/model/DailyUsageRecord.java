package com.example.aiproxyplatform.model;

import java.time.LocalDate;

public class DailyUsageRecord {

    private final LocalDate date;
    private int tokensConsumed;
    private int requestsCount;

    public DailyUsageRecord(LocalDate date) {
        this.date = date;
        this.tokensConsumed = 0;
        this.requestsCount = 0;
    }

    public void addTokens(int tokens) {
        this.tokensConsumed += tokens;
    }

    public void addRequest() {
        this.requestsCount++;
    }

    public LocalDate getDate() {
        return date;
    }

    public int getTokensConsumed() {
        return tokensConsumed;
    }

    public int getRequestsCount() {
        return requestsCount;
    }
}
