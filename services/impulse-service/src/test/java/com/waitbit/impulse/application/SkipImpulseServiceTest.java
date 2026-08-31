package com.waitbit.impulse.application;

import com.waitbit.impulse.domain.Impulse;
import com.waitbit.impulse.domain.ImpulseStatus;
import com.waitbit.impulse.domain.InvalidImpulseStateException;
import com.waitbit.impulse.domain.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Currency;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SkipImpulseServiceTest {

    @Mock
    private ImpulseRepository impulseRepository;

    private SkipImpulseService service;

    @BeforeEach
    void setUp() {
        service = new SkipImpulseService(
                impulseRepository
        );
    }

    @Test
    void shouldSkipQuarantinedImpulse() {
        UUID id = UUID.randomUUID();

        Impulse impulse = createImpulse(id);

        when(impulseRepository.findById(id))
                .thenReturn(Optional.of(impulse));

        when(impulseRepository.update(impulse))
                .thenReturn(impulse);

        Impulse result = service.skip(id);

        assertSame(impulse, result);

        assertEquals(
                ImpulseStatus.SKIPPED,
                result.status()
        );

        verify(impulseRepository).findById(id);
        verify(impulseRepository).update(impulse);
    }

    @Test
    void shouldThrowWhenImpulseDoesNotExist() {
        UUID id = UUID.randomUUID();

        when(impulseRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(
                ImpulseNotFoundException.class,
                () -> service.skip(id)
        );

        verify(impulseRepository, never())
                .update(any());
    }

    @Test
    void shouldRejectSkippingPurchasedImpulse() {
        UUID id = UUID.randomUUID();

        Impulse impulse = createImpulse(id);

        impulse.purchase();

        when(impulseRepository.findById(id))
                .thenReturn(Optional.of(impulse));

        assertThrows(
                InvalidImpulseStateException.class,
                () -> service.skip(id)
        );

        verify(impulseRepository, never())
                .update(any());
    }

    private Impulse createImpulse(UUID id) {
        return Impulse.quarantine(
                id,
                "Sony WH-1000XM6",
                new Money(
                        new BigDecimal("19499.90"),
                        Currency.getInstance("TRY")
                ),
                Instant.parse("2026-08-31T12:30:22Z")
        );
    }
}