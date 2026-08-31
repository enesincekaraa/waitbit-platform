package com.waitbit.impulse.web;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateImpulseRequest(

        @NotBlank(
                message = "productName must not be blank"
        )
        String productName,

        @NotNull(
                message = "amount must not be null"
        )
        @DecimalMin(
                value = "0.01",
                message = "amount must be at least 0.01"
        )
        BigDecimal amount,

        @NotBlank(
                message = "currency must not be blank"
        )
        String currency
) {
}