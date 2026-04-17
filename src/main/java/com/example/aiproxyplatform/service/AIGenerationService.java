package com.example.aiproxyplatform.service;

import com.example.aiproxyplatform.dto.GenerationRequest;
import com.example.aiproxyplatform.dto.GenerationResponse;

public interface AIGenerationService {

    GenerationResponse generate(GenerationRequest request);
}
