package com.opportunity.deliveryservice.payment.infrastructure;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.opportunity.deliveryservice.payment.infrastructure.config.TossFeignConfig;
import com.opportunity.deliveryservice.payment.infrastructure.dto.TossConfirmRequest;
import com.opportunity.deliveryservice.payment.infrastructure.dto.TossConfirmResponse;

@FeignClient(
        name = "tossPaymentsClient",
        url = "${toss.base-url}",
        configuration = TossFeignConfig.class
)
public interface TossPaymentsClient {
    @PostMapping("/v1/payments/confirm")
    TossConfirmResponse confirm(@RequestBody TossConfirmRequest request);
}