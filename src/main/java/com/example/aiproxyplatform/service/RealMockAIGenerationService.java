package com.example.aiproxyplatform.service;

import com.example.aiproxyplatform.dto.GenerationRequest;
import com.example.aiproxyplatform.dto.GenerationResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
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

    private final Random random = new Random();

    @Override
    public GenerationResponse generate(GenerationRequest request) {
        String prompt = request.getPrompt() != null ? request.getPrompt().trim() : "";

        simulateLatency();

        String generatedText = generateMockText();
        int tokensConsumed = calculateTokens(prompt);

        return GenerationResponse.builder()
                .userId(request.getUserId())
                .prompt(prompt)
                .generatedText(generatedText)
                .tokensConsumed(tokensConsumed)
                .plan("UNKNOWN")           // filled by proxy in the next phase
                .remainingRequestsInWindow(0)   // filled by proxy in the next phase
                .remainingMonthlyTokens(0)      // filled by proxy in the next phase
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

    private int calculateTokens(String prompt) {
        return Math.max(10, (int) Math.ceil(prompt.length() / 4.0));
    }
}
