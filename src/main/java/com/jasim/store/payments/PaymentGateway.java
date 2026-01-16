package com.jasim.store.payments;

import com.jasim.store.entities.Order;

import java.util.Optional;

public interface PaymentGateway {
    CheckoutSession createCheckoutSession(Order order);
    Optional<PaymentResult> parseWebhookEvent(WebhookRequest request);
}
