package com.jasim.store.payments;

import com.jasim.store.entities.Order;
import com.jasim.store.exceptions.CartNotFoundException;
import com.jasim.store.exceptions.EmptyCartFoundException;
import com.jasim.store.repositories.CartRepository;
import com.jasim.store.repositories.OrderRepository;
import com.jasim.store.services.AuthService;
import com.jasim.store.services.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CheckoutService {
    private final CartRepository cartRepository;
    private final AuthService authService;
    private final CartService cartService;
    private final OrderRepository orderRepository;
    private final PaymentGateway paymentGateway;


    @Transactional
    public CheckoutResponse checkoutResponse(CheckoutRequest request) {
        var cart = cartRepository.findCartById(request.getCartId()).orElse(null);
        if (cart == null){
            throw new CartNotFoundException();
        }
        if (cart.isCartEmpty()){
            throw new EmptyCartFoundException();
        }

        var order = new Order().fromCart(cart, authService.getCurrentUser()); //moved order logic from here to Order entity

        orderRepository.save(order);
       try {
           var session = paymentGateway.createCheckoutSession(order);
           cartService.deleteAllItemsOfCart(cart.getId());
           return new CheckoutResponse(order.getId(), session.getCheckoutUrl());
       }
       catch (PaymentGatewayException ex){
           orderRepository.delete(order);
           throw  ex;
       }
    }

    public void handleWebhookEvent(WebhookRequest request){
        paymentGateway.parseWebhookEvent(request)
                .ifPresent(paymentResult -> {
                    var order = orderRepository.findById(paymentResult.getOrderId()).orElseThrow();
                    order.setStatus(paymentResult.getPaymentStatus());
                    orderRepository.save(order);
                });
    }



}
