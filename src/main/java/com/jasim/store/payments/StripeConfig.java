package com.jasim.store.payments;

import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StripeConfig {
    @Value("${stripe.secret_key}")
    private String secret_key;

    @PostConstruct
    public void init(){
        Stripe.apiKey = secret_key;
    }
}
