package com.example.aiproxyplatform.service;

import com.example.aiproxyplatform.dto.GenerationRequest;
import com.example.aiproxyplatform.dto.GenerationResponse;
import com.example.aiproxyplatform.exception.QuotaExceededException;
import com.example.aiproxyplatform.exception.UserNotFoundException;
import com.example.aiproxyplatform.model.UserAccount;
import com.example.aiproxyplatform.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;

public class QuotaProxyService implements AIGenerationService {

    private static final Logger log = LoggerFactory.getLogger(QuotaProxyService.class);

    private final AIGenerationService target;
    private final UserRepository userRepository;
    private final TokenEstimatorService tokenEstimator;

    public QuotaProxyService(AIGenerationService target,
                             UserRepository userRepository,
                             TokenEstimatorService tokenEstimator) {
        this.target = target;
        this.userRepository = userRepository;
        this.tokenEstimator = tokenEstimator;
    }

    @Override
    public GenerationResponse generate(GenerationRequest request) {
        UserAccount user = getUserOrThrow(request.getUserId());

        resetQuotaIfNeeded(user);

        String prompt = request.getPrompt() != null ? request.getPrompt().trim() : "";
        int estimatedTokens = tokenEstimator.estimate(prompt);

        validateQuota(user, estimatedTokens);

        GenerationResponse response = target.generate(request);

        user.recordDailyUsage(LocalDate.now(), response.getTokensConsumed());
        userRepository.save(user);

        return enrichResponse(response, user);
    }

    private UserAccount getUserOrThrow(String userId) {
        return userRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    private void resetQuotaIfNeeded(UserAccount user) {
        if (!LocalDate.now().isBefore(user.getQuotaResetDate())) {
            user.resetMonthlyQuota();
        }
    }

    private void validateQuota(UserAccount user, int estimatedTokens) {
        if (!user.hasMonthlyTokensAvailable(estimatedTokens)) {
            LocalDate resetDate = user.getQuotaResetDate();
            log.warn("Monthly quota exceeded — user: '{}', plan: {}, resetDate: {}.",
                    user.getUserId(), user.getPlanType().name(), resetDate);
            throw new QuotaExceededException(
                    "Monthly token quota exceeded for plan " + user.getPlanType().name()
                    + ". Quota resets on " + resetDate + "."
            );
        }
    }

    private GenerationResponse enrichResponse(GenerationResponse response, UserAccount user) {
        int remainingMonthly = user.getPlanType().isUnlimited() ? -1 : user.getRemainingMonthlyTokens();

        return GenerationResponse.builder()
                .userId(response.getUserId())
                .prompt(response.getPrompt())
                .generatedText(response.getGeneratedText())
                .tokensConsumed(response.getTokensConsumed())
                .plan(user.getPlanType().name())
                .remainingRequestsInWindow(response.getRemainingRequestsInWindow())
                .remainingMonthlyTokens(remainingMonthly)
                .timestamp(response.getTimestamp())
                .build();
    }

    // First day of the month following the given date
    private LocalDate calculateNextQuotaResetDate() {
        return LocalDate.now().plusMonths(1).withDayOfMonth(1);
    }
}
