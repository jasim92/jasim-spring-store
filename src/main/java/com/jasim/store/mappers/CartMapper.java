package com.jasim.store.mappers;

import com.jasim.store.dtos.CartDto;
import com.jasim.store.dtos.CartItemDto;
import com.jasim.store.entities.Cart;
import com.jasim.store.entities.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartMapper {

    @Mapping(target = "totalPrice", expression = "java(cart.getTotalPrice())")
    CartDto toDto(Cart cart);
    @Mapping(target = "totalPrice", expression = "java(cartItem.getTotalPrice())")
    CartItemDto toCartProductDto(CartItem cartItem);
}
