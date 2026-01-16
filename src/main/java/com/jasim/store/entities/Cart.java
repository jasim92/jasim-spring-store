package com.jasim.store.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "carts", schema = "store")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "date_created", insertable = false, updatable = false)
    private LocalDate dateCreated;

    // orphan removal used because it will allow to its child cartItems as per our db schema
    @OneToMany(mappedBy = "cart", cascade = CascadeType.MERGE, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<CartItem> items = new LinkedHashSet<>();

    public BigDecimal getTotalPrice(){
        return items.stream().map(CartItem::getTotalPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public CartItem getCartItems(Long productId){
        return items.stream().filter(items -> items.getProduct().getId().equals(productId))
                .findFirst().orElse(null);
    }

    public CartItem addCart(Product product){
        var cartItems = getCartItems(product.getId());

        if (cartItems != null){
            cartItems.setQuantity(cartItems.getQuantity()+1);
        }else {
            cartItems = new CartItem();
            cartItems.setProduct(product);
            cartItems.setQuantity(1);
            cartItems.setCart(this);

            items.add(cartItems);
        }
        return cartItems;
    }

    public void removeCartItem(Long productId){

        var cartItem = getCartItems(productId);

        if (cartItem != null){
           items.remove(cartItem);
           cartItem.setCart(null);
        }
    }

    public void clear(){
        items.clear();
    }

    public boolean isCartEmpty(){
        return items.isEmpty();
    }
}