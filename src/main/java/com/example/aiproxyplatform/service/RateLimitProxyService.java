package com.example.aiproxyplatform.service;

import com.example.aiproxyplatform.dto.GenerationRequest;
import com.example.aiproxyplatform.dto.GenerationResponse;
import com.example.aiproxyplatform.exception.RateLimitExceededException;
import com.example.aiproxyplatform.exception.UserNotFoundException;
import com.example.aiproxyplatform.model.UserAccount;
import com.example.aiproxyplatform.repository.UserRepository;

import java.time.Duration;
import java.time.LocalDateTime;

public class RateLimitProxyService implements AIGenerationService {

    private final AIGenerationService target;
    private final UserRepository userRepository;

    public RateLimitProxyService(AIGenerationService target, UserRepository userRepository) {
        this.target = target;
        this.userRepository = userRepository;
    }

    @Override
    public GenerationResponse generate(GenerationRequest request) {
        UserAccount user = getUserOrThrow(request.getUserId());

        resetWindowIfNeeded(user);
        validateRateLimit(user);

        user.incrementRequestsInWindow();
        userRepository.save(user);

        GenerationResponse response = target.generate(request);

        return enrichResponse(response, user);
    }

    private UserAccount getUserOrThrow(String userId) {
        return userRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    private void resetWindowIfNeeded(UserAccount user) {
        LocalDateTime windowEnd = user.getRateLimitWindowStart().plusMinutes(1);
        if (LocalDateTime.now().isAfter(windowEnd)) {
            user.resetRateLimitWindow();
        }
    }

    private void validateRateLimit(UserAccount user) {
        if (!user.hasRequestsAvailableInWindow()) {
            long retryAfter = calculateRetryAfterSeconds(user);
            throw new RateLimitExceededException(
                    "Rate limit exceeded for plan " + user.getPlanType().name()
                    + ". Limit: " + user.getPlanType().getRequestsPerMinute() + " req/min.",
                    retryAfter
            );
        }
    }

    private long calculateRetryAfterSeconds(UserAccount user) {
        LocalDateTime windowEnd = user.getRateLimitWindowStart().plusMinutes(1);
        long seconds = Duration.between(LocalDateTime.now(), windowEnd).getSeconds();
        return Math.max(0, seconds);
    }

    private GenerationResponse enrichResponse(GenerationResponse response, UserAccount user) {
        return GenerationResponse.builder()
                .userId(response.getUserId())
                .prompt(response.getPrompt())
                .generatedText(response.getGeneratedText())
                .tokensConsumed(response.getTokensConsumed())
                .plan(user.getPlanType().name())
                .remainingRequestsInWindow(user.getRemainingRequestsInWindow())
                .remainingMonthlyTokens(response.getRemainingMonthlyTokens())
                .timestamp(response.getTimestamp())
                .build();
    }
}
