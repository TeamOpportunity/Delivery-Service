package com.opportunity.deliveryservice.gemini.application.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.opportunity.deliveryservice.gemini.domain.entity.AiRequestHistory;
import com.opportunity.deliveryservice.gemini.domain.repository.AiRequestHistoryRepository;
import com.opportunity.deliveryservice.gemini.infrastructure.GeminiClient;
import com.opportunity.deliveryservice.gemini.infrastructure.dto.GeminiRequest;
import com.opportunity.deliveryservice.gemini.infrastructure.dto.GeminiResponse;
import com.opportunity.deliveryservice.product.domain.entity.Product;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GeminiService {

	private final AiRequestHistoryRepository aiRequestHistoryRepository;
	private final GeminiClient geminiClient;

	@Value("${gemini.api-key}")
	private String apiKey;

	@Transactional
	public String createProductDescription(String prompt, Product product) {
		String response = askGemini(prompt);

		AiRequestHistory aiRequestHistory = AiRequestHistory.builder().requestPrompt(prompt)
			.response(response)
			.product(product)
			.build();

		aiRequestHistoryRepository.save(aiRequestHistory);

		return response;
	}

	private String askGemini(String prompt) {
		String question = prompt + "의 상품 설명을 참고하여 배달 앱의 상품 설명란에 들어갈 문구를 만들어주세요. 단, 100자 이내로 간결하게 한 문장만 생성하고, 불필요한 문구나 추가 설명은 제외해주세요.";
		GeminiRequest.Part part = new GeminiRequest.Part(question);
		GeminiRequest.Content content = new GeminiRequest.Content(List.of(part));
		GeminiRequest request = new GeminiRequest(List.of(content));

		GeminiResponse response = geminiClient.generateContent(apiKey, request);
		return response.getCandidates()
			.get(0)
			.getContent()
			.getParts()
			.get(0)
			.getText();
	}
}
