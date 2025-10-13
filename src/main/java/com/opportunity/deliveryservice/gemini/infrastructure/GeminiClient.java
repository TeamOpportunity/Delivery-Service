package com.opportunity.deliveryservice.gemini.infrastructure;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.opportunity.deliveryservice.gemini.infrastructure.dto.GeminiRequest;
import com.opportunity.deliveryservice.gemini.infrastructure.dto.GeminiResponse;
import com.opportunity.deliveryservice.global.config.OpenFeignConfig;

@FeignClient(
	name = "gemini-client",
	url = "${gemini.url}",
	configuration = OpenFeignConfig.class)
public interface GeminiClient {
	@PostMapping("/models/gemini-2.5-flash:generateContent")
	GeminiResponse generateContent(
		@RequestHeader("x-goog-api-key") String apiKey,
		@RequestBody GeminiRequest request
	);
}