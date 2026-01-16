package com.jasim.store.payments;

import com.jasim.store.entities.Order;
import com.jasim.store.entities.PaymentStatus;
import com.jasim.store.repositories.OrderRepository;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StripePaymentGateway implements PaymentGateway{
    @Value("${website.url}")
    private String website_url;
    @Value("${stripe.webhook_secret_key}")
    private String webhookSecretKey;
    private final OrderRepository orderRepository;

    @Override
    public CheckoutSession createCheckoutSession(Order order) {
        try {
            var builder = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(website_url+"/checkout-success?orderId="+order.getId())
                    .setCancelUrl(website_url+"/checkout-cancel")
                            .setPaymentIntentData(SessionCreateParams.PaymentIntentData.builder()
                                    .putMetadata("order_id",order.getId().toString()).build()
                            );
            order.getItems().forEach(orderItem -> {
                var lineItem = SessionCreateParams.LineItem.builder()
                        .setQuantity(Long.valueOf(orderItem.getQuantity()))
                        .setPriceData(
                                SessionCreateParams.LineItem.PriceData.builder()
                                        .setCurrency("aed")
                                        .setUnitAmountDecimal(orderItem.getUnitPrice().multiply(BigDecimal.valueOf(100)))
                                        .setProductData(
                                                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                        .setName(orderItem.getProduct().getName()).build()
                                        ).build()
                        ).build();
                builder.addLineItem(lineItem);
            });

            var session = Session.create(builder.build());
            return new CheckoutSession(session.getUrl());
        }
        catch (StripeException ex){
            throw new PaymentGatewayException(ex.getMessage());
        }
    }

    @Override
    public Optional<PaymentResult> parseWebhookEvent(WebhookRequest request) {
        try{
            var payload = request.getPayload();
            var signature = request.getHeader().get("stripe-signature");
            var event = Webhook.constructEvent(payload,signature,webhookSecretKey);
            var orderId = extractOrderId(event);

            switch (event.getType()){

                case "payment_intent.succeeded" -> {
                    return Optional.of(new PaymentResult(orderId, PaymentStatus.PAID));
                }
                case "payment_intent.payment_failed" -> {
                    return Optional.of(new PaymentResult(orderId, PaymentStatus.FAILED));
                }
                default -> {
                    return Optional.empty();
                }
            }

        }
        catch (SignatureVerificationException ex){
            throw new PaymentGatewayException("Invalid Signature");
        }

    }

    private Long extractOrderId(Event event){
        var stripeObject = event.getDataObjectDeserializer().getObject().orElseThrow(
                () -> new PaymentGatewayException("Could not deserialize Stripe event. Check the SDK and API version.")
        );
        var payment_intent = (PaymentIntent) stripeObject;
        var orderId =  payment_intent.getMetadata().get("order_id");
        return Long.valueOf(orderId);
    }
}
