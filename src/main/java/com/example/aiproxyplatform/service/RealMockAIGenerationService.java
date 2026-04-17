package com.example.aiproxyplatform.service;

import com.example.aiproxyplatform.dto.GenerationRequest;
import com.example.aiproxyplatform.dto.GenerationResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

public class RealMockAIGenerationService implements AIGenerationService {

    private static final int LATENCY_MS = 1200;

    private static final List<String> MOCK_RESPONSES = List.of(
            "This is a simulated AI response based on your prompt.",
            "Your request was processed successfully by the mock AI service.",
            "This platform is currently using a simulated text generation engine.",
            "Here is a generated response produced by the mock backend service.",
            "The mock AI has analyzed your input and produced this sample output.",
            "Simulated generation complete. Your prompt was received and processed."
    );

    private final TokenEstimatorService tokenEstimator;
    private final Random random = new Random();

    public RealMockAIGenerationService(TokenEstimatorService tokenEstimator) {
        this.tokenEstimator = tokenEstimator;
    }

    @Override
    public GenerationResponse generate(GenerationRequest request) {
        String prompt = request.getPrompt() != null ? request.getPrompt().trim() : "";

        simulateLatency();

        return GenerationResponse.builder()
                .userId(request.getUserId())
                .prompt(prompt)
                .generatedText(generateMockText())
                .tokensConsumed(tokenEstimator.estimate(prompt))
                .plan("UNKNOWN")                // filled by quota proxy
                .remainingRequestsInWindow(0)   // filled by rate-limit proxy
                .remainingMonthlyTokens(0)      // filled by quota proxy
                .timestamp(LocalDateTime.now())
                .build();
    }

    private void simulateLatency() {
        try {
            Thread.sleep(LATENCY_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private String generateMockText() {
        return MOCK_RESPONSES.get(random.nextInt(MOCK_RESPONSES.size()));
    }
}
