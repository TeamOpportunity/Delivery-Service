package com.opportunity.deliveryservice.payment.infrastructure;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.opportunity.deliveryservice.payment.infrastructure.config.TossFeignConfig;
import com.opportunity.deliveryservice.payment.infrastructure.dto.TossConfirmRequest;
import com.opportunity.deliveryservice.payment.infrastructure.dto.TossConfirmResponse;

import jakarta.websocket.server.PathParam;

@FeignClient(
        name = "tossPaymentsClient",
        url = "${toss.base-url}",
        configuration = TossFeignConfig.class
)
public interface TossPaymentsClient {
    @PostMapping("/v1/payments/confirm")
    TossConfirmResponse confirm(@RequestBody TossConfirmRequest request);

    @GetMapping("/v1/payments/{paymentKey}")
    TossConfirmResponse getPaymentInfo(@PathVariable String paymentKey);
}