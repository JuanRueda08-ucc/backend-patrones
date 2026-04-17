package com.example.aiproxyplatform.controller;

import com.example.aiproxyplatform.dto.GenerationRequest;
import com.example.aiproxyplatform.dto.GenerationResponse;
import com.example.aiproxyplatform.service.AIGenerationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final AIGenerationService aiGenerationService;

    public AiController(AIGenerationService aiGenerationService) {
        this.aiGenerationService = aiGenerationService;
    }

    @PostMapping("/generate")
    public ResponseEntity<GenerationResponse> generate(@RequestBody @Valid GenerationRequest request) {
        GenerationResponse response = aiGenerationService.generate(request);
        return ResponseEntity.ok(response);
    }
}
