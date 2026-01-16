package com.jasim.store.services;

import com.jasim.store.dtos.CartDto;
import com.jasim.store.dtos.CartItemDto;
import com.jasim.store.entities.Cart;
import com.jasim.store.exceptions.CartNotFoundException;
import com.jasim.store.exceptions.ProductNotFoundException;
import com.jasim.store.mappers.CartMapper;
import com.jasim.store.repositories.CartRepository;
import com.jasim.store.repositories.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@AllArgsConstructor
@Service
public class CartService {

    private CartRepository cartRepository;
    private CartMapper cartMapper;
    private ProductRepository productRepository;

    public CartDto createCart() {
        var cart = new Cart();
        cartRepository.save(cart);
        return cartMapper.toDto(cart);
    }

    public CartItemDto addToCart(UUID cart_id, Long productId) {

        var cart = cartRepository.findCartById(cart_id).orElse(null);
        if (cart == null) {
            throw new CartNotFoundException();
        }

        var product = productRepository.findById(productId).orElse(null);

        if (product == null) {
            throw new ProductNotFoundException();
        }

        // we moved logic from here to Cart class because this business logic is related to Cart
        //that's Object oriented programming to keep related logic into related class
        var cartItems = cart.addCart(product);

        cartRepository.save(cart);

        return cartMapper.toCartProductDto(cartItems);
    }

    public CartDto getCart(UUID cart_id) {
        var cart = cartRepository.findCartById(cart_id).orElse(null);
        if (cart == null) {
            throw new CartNotFoundException();
        }
        return cartMapper.toDto(cart);
    }

    public CartItemDto updateCart(UUID cartId, Long productId, Integer quantity) {
        var cart = cartRepository.findCartById(cartId).orElse(null);
        if (cart == null) {
            throw new CartNotFoundException();
        }
// we moved logic from here to Cart class because this business logic is related to Cart
        var cartItem = cart.getCartItems(productId);

        if (cartItem == null) {
           throw new CartNotFoundException();
        }
        cartItem.setQuantity(quantity);
        cartRepository.save(cart);
        return cartMapper.toCartProductDto(cartItem);
    }

    public void deleteCartItem(UUID cartId, Long productId){
        var cart = cartRepository.findCartById(cartId).orElse(null);
        if (cart == null) {
            throw new CartNotFoundException();
        }

        cart.removeCartItem(productId);
        cartRepository.save(cart);
    }

    public void deleteAllItemsOfCart(UUID cartId){
        var cart = cartRepository.findCartById(cartId).orElse(null);
        if (cart == null){
            throw new CartNotFoundException();
        }

        cart.clear();
        cartRepository.save(cart);
    }
}
