package com.waitbit.impulse.web;

public final class InvalidCurrencyCodeException extends RuntimeException {
    public InvalidCurrencyCodeException(String currencyCode) {
        super("Unsupported currency code: " + currencyCode);
    }
}
