package com.waitbit.impulse.infrastructure.persistence;

import com.waitbit.impulse.domain.Impulse;
import com.waitbit.impulse.domain.Money;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Currency;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InMemoryImpulseRepositoryTest {

    private static final Money PRICE =
            new Money(
                    new BigDecimal("19499.90"),
                    Currency.getInstance("TRY")
            );

    private static final Instant CREATED_AT =
            Instant.parse("2026-08-31T10:00:00Z");

    @Test
    void shouldSaveImpulse() {
        InMemoryImpulseRepository repository =
                new InMemoryImpulseRepository();

        Impulse impulse = Impulse.quarantine(
                UUID.randomUUID(),
                "Sony WH-1000XM6",
                PRICE,
                CREATED_AT
        );

        Impulse saved = repository.save(impulse);

        assertSame(
                impulse,
                saved
        );
    }

    @Test
    void shouldRejectDuplicateImpulseId() {
        InMemoryImpulseRepository repository =
                new InMemoryImpulseRepository();

        UUID id = UUID.randomUUID();

        Impulse firstImpulse = Impulse.quarantine(
                id,
                "Sony WH-1000XM6",
                PRICE,
                CREATED_AT
        );

        Impulse anotherImpulseWithSameId =
                Impulse.quarantine(
                        id,
                        "Nike Air Max",
                        new Money(
                                new BigDecimal("4500"),
                                Currency.getInstance("TRY")
                        ),
                        CREATED_AT
                );

        repository.save(firstImpulse);

        assertThrows(
                IllegalStateException.class,
                () -> repository.save(anotherImpulseWithSameId)
        );
    }
}