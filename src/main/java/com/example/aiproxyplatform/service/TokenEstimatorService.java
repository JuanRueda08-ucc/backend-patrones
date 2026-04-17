package com.example.aiproxyplatform.service;

import org.springframework.stereotype.Service;

@Service
public class TokenEstimatorService {

    public int estimate(String prompt) {
        String safePrompt = (prompt != null) ? prompt : "";
        return Math.max(10, (int) Math.ceil(safePrompt.length() / 4.0));
    }
}
