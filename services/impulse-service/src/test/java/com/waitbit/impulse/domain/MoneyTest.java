package com.waitbit.impulse.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MoneyTest {

    private static final Currency TRY =  Currency.getInstance("TRY");


    @Test
    void shouldCreateMoneyWithPositiveAmount() {
        Money money = new Money(
                new BigDecimal("1500.00"),
                TRY
        );

        assertEquals(new BigDecimal("1500.00"),money.amount());
        assertEquals(TRY, money.currency());

    }

    @ParameterizedTest
    @ValueSource(strings = {"0","-100","-1","-0.01"})
    void shouldRejectNonPositiveAmount(BigDecimal invalidAmount) {
        assertThrows(
                IllegalArgumentException.class,
                ()-> new Money(invalidAmount,TRY)
        );
    }

    @Test
    void shouldRejectNullCurrency() {
        assertThrows(
                NullPointerException.class,
                () -> new Money(new BigDecimal("100"), null)
        );
    }
}
