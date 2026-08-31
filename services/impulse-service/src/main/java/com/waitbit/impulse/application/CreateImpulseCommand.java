package com.waitbit.impulse.application;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;

public record CreateImpulseCommand(
        String productName,
        BigDecimal amount,
        Currency currency
) {

    public CreateImpulseCommand {
        Objects.requireNonNull(productName, "productName must not be null");
        Objects.requireNonNull(amount, "amount must not be null");
        Objects.requireNonNull(currency, "currency must not be null");
    }
}