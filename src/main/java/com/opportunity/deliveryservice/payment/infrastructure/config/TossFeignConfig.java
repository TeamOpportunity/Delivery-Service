package com.opportunity.deliveryservice.payment.infrastructure.config;

import feign.Logger;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class TossFeignConfig {

    @Value("${toss.secret-key}")
    private String secretKey;

    @Bean
    Logger.Level feignLoggerLevel() {
        // FULL : 요청/응답 헤더 + 바디 + 메타데이터 전체 출력
        // BASIC : 요청 메서드, URL, 응답 상태 코드, 실행시간
        // HEADERS : BASIC + 요청/응답 헤더
        // NONE : 로깅 없음
        return Logger.Level.BASIC;
    }

    @Bean
    public RequestInterceptor tossAuthInterceptor() {
        return template -> {
            String token = Base64.getEncoder()
                    .encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));
            template.header("Authorization", "Basic " + token);
            template.header("Content-Type", "application/json");
            template.header("Accept", "application/json");
        };
    }

    @Bean
    public ErrorDecoder tossErrorDecoder() {
        return new TossFeignErrorDecoder();
    }
}