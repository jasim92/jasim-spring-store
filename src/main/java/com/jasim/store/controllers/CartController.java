package com.jasim.store.controllers;

import com.jasim.store.dtos.*;
import com.jasim.store.exceptions.CartNotFoundException;
import com.jasim.store.exceptions.ProductNotFoundException;
import com.jasim.store.services.CartService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("/carts")
@Tag(name = "Carts")
public class CartController {
    private final CartService cartService;

    @PostMapping
    public ResponseEntity<CartDto> createCart(UriComponentsBuilder uriBuilder) {
        var cartDto = cartService.createCart();
        var uri = uriBuilder.path("carts/{id}").buildAndExpand(cartDto.getId()).toUri();
        return ResponseEntity.created(uri).body(cartDto);
    }

    @PostMapping("/{cart_id}/items")
    public ResponseEntity<CartItemDto> addToCart(@PathVariable UUID cart_id,
                                                 @RequestBody AddItemToCartRequest request) {
        var cartItemDto = cartService.addToCart(cart_id, request.getProductId());
        return ResponseEntity.status(HttpStatus.CREATED).body(cartItemDto);

    }

    @GetMapping("/{cart_id}")
    public ResponseEntity<CartDto> getCart(@PathVariable UUID cart_id) {

       var cartDto = cartService.getCart(cart_id);
        return ResponseEntity.ok(cartDto);
    }

    @PutMapping("/{cartId}/items/{productId}")
    public ResponseEntity<?> updateCart(
            @PathVariable UUID cartId,
            @PathVariable Long productId,
            @Valid @RequestBody updateCartItemRequest request
    ) {

        var cartItemDto = cartService.updateCart(cartId,productId,request.getQuantity());
        return ResponseEntity.ok(cartItemDto);

    }

    @DeleteMapping("/{cartId}/items/{productId}")
    public ResponseEntity<?> deleteCartItem(
            @PathVariable UUID cartId,
            @PathVariable Long productId
    ) {
        cartService.deleteCartItem(cartId,productId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{cartId}/items")
    public ResponseEntity<Void> clearCart(@PathVariable UUID cartId){

       cartService.deleteAllItemsOfCart(cartId);
        return ResponseEntity.noContent().build();

    }

    @ExceptionHandler(CartNotFoundException.class)
    public ResponseEntity<ErrorDto> handleCartNotFound(){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
//                Map.of("error","Cart not found")
                new ErrorDto("cart not found")
        );
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorDto> handleProductNotFound(){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
//                Map.of("error","Product not found in Cart")
                new ErrorDto("Product not found in cart")
        );
    }
}
