package com.jasim.store.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class updateCartItemRequest {
    @NotNull(message = "please provide quantity")
    @Min(value = 1, message = "quantity must greater than zero")
    @Max(value = 1000, message = "quantity must less than 1000")
    private Integer quantity;
}
