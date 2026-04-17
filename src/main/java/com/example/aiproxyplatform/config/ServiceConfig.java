package com.example.aiproxyplatform.config;

import com.example.aiproxyplatform.repository.UserRepository;
import com.example.aiproxyplatform.service.AIGenerationService;
import com.example.aiproxyplatform.service.QuotaProxyService;
import com.example.aiproxyplatform.service.RateLimitProxyService;
import com.example.aiproxyplatform.service.RealMockAIGenerationService;
import com.example.aiproxyplatform.service.TokenEstimatorService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class ServiceConfig {

    /**
     * Layer 1 (innermost): the real AI service that generates text.
     * It has no access control — it just processes the request.
     */
    @Bean
    public RealMockAIGenerationService realMockAIGenerationService(TokenEstimatorService tokenEstimator) {
        return new RealMockAIGenerationService(tokenEstimator);
    }

    /**
     * Layer 2 (middle): quota proxy wraps the real service.
     * Enforces monthly token limits before delegating inward.
     */
    @Bean
    public QuotaProxyService quotaProxyService(RealMockAIGenerationService realService,
                                               UserRepository userRepository,
                                               TokenEstimatorService tokenEstimator) {
        return new QuotaProxyService(realService, userRepository, tokenEstimator);
    }

    /**
     * Layer 3 (outermost): rate-limit proxy wraps the quota proxy.
     * Enforces requests-per-minute limits before delegating inward.
     * Marked @Primary so it is the default AIGenerationService injected everywhere.
     */
    @Bean
    @Primary
    public AIGenerationService aiGenerationService(QuotaProxyService quotaProxy,
                                                    UserRepository userRepository) {
        return new RateLimitProxyService(quotaProxy, userRepository);
    }
}
