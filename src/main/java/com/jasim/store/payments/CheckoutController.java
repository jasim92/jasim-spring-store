package com.jasim.store.payments;

import com.jasim.store.dtos.ErrorDto;
import com.jasim.store.exceptions.CartNotFoundException;
import com.jasim.store.exceptions.EmptyCartFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/checkout")
public class CheckoutController {
    private final CheckoutService checkoutService;


    @PostMapping
    public ResponseEntity<?> checkout(@Valid @RequestBody CheckoutRequest request) {

        var response = checkoutService.checkoutResponse(request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/webhook")
    public void handleWebhook(@RequestHeader Map<String ,String> header,
                                           @RequestBody String payload){
        checkoutService.handleWebhookEvent(new WebhookRequest(header,payload));
    }

    @ExceptionHandler(PaymentGatewayException.class)
    public ResponseEntity<ErrorDto> handlePaymentGatewayException() {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ErrorDto("error during creating a checkout session")
        );
    }

    @ExceptionHandler(CartNotFoundException.class)
    public ResponseEntity<ErrorDto> handleCartNotFound() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
//                Map.of("error","Cart not found")
                new ErrorDto("cart not foundx")
        );
    }

    @ExceptionHandler(EmptyCartFoundException.class)
    public ResponseEntity<ErrorDto> handleEmptyCartFound() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
//                Map.of("error","Cart not found")
                new ErrorDto("Empty cart foundx")
        );
    }

}
