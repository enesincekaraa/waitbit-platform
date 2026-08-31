package com.waitbit.impulse.infrastructure.persistence;

import com.waitbit.impulse.domain.Impulse;
import com.waitbit.impulse.domain.Money;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Currency;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    void shouldFindSavedImpulseById(){
        InMemoryImpulseRepository repository =
                new InMemoryImpulseRepository();
        UUID id = UUID.randomUUID();

        Impulse impulse = Impulse.quarantine(
                id,
                "Sony WH-1000XM6",
                PRICE,
                CREATED_AT
        );

        repository.save(impulse);

        var result = repository.findById(id);

        assertTrue(result.isPresent());
        assertSame(impulse, result.get());
    }

    @Test
    void shouldReturnEmptyWhenImpulseDoesNotExist() {
        InMemoryImpulseRepository repository =
                new InMemoryImpulseRepository();

        var result =
                repository.findById(UUID.randomUUID());

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldUpdateExistingImpulse() {
        InMemoryImpulseRepository repository =
                new InMemoryImpulseRepository();

        UUID id = UUID.randomUUID();

        Impulse original = Impulse.quarantine(
                id,
                "PlayStation 5 Pro",
                PRICE,
                CREATED_AT
        );

        repository.save(original);

        Impulse updated = Impulse.quarantine(
                id,
                "PlayStation 5 Pro - Updated",
                new Money(
                        new BigDecimal("39999.90"),
                        Currency.getInstance("TRY")
                ),
                CREATED_AT
        );

        Impulse result = repository.update(updated);

        assertSame(updated, result);

        assertSame(
                updated,
                repository.findById(id).orElseThrow()
        );
    }

    @Test
    void shouldRejectUpdatingMissingImpulse() {
        InMemoryImpulseRepository repository =
                new InMemoryImpulseRepository();

        Impulse impulse = Impulse.quarantine(
                UUID.randomUUID(),
                "PlayStation 5 Pro",
                PRICE,
                CREATED_AT
        );

        assertThrows(
                IllegalStateException.class,
                () -> repository.update(impulse)
        );
    }

}