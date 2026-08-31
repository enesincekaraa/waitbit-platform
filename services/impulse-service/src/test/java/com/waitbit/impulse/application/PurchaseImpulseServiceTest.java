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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PurchaseImpulseServiceTest {

    private static final Instant CREATED_AT =
            Instant.parse("2026-08-31T12:30:22Z");

    @Mock
    private ImpulseRepository impulseRepository;

    private PurchaseImpulseService service;

    @BeforeEach
    void setUp() {
        service = new PurchaseImpulseService(
                impulseRepository
        );
    }

    @Test
    void shouldPurchaseQuarantinedImpulse() {
        UUID id = UUID.randomUUID();

        Impulse impulse = createImpulse(id);

        when(impulseRepository.findById(id))
                .thenReturn(Optional.of(impulse));

        when(impulseRepository.update(impulse))
                .thenReturn(impulse);

        Impulse result =
                service.purchase(id);

        assertSame(
                impulse,
                result
        );

        assertEquals(
                ImpulseStatus.PURCHASED,
                result.status()
        );

        verify(impulseRepository).findById(id);
        verify(impulseRepository).update(impulse);
    }

    @Test
    void shouldPurchaseReadyForDecisionImpulse() {
        UUID id = UUID.randomUUID();

        Impulse impulse = createImpulse(id);

        impulse.makeReadyForDecision(
                CREATED_AT.plusSeconds(7 * 24 * 60 * 60)
        );

        when(impulseRepository.findById(id))
                .thenReturn(Optional.of(impulse));

        when(impulseRepository.update(impulse))
                .thenReturn(impulse);

        Impulse result =
                service.purchase(id);

        assertEquals(
                ImpulseStatus.PURCHASED,
                result.status()
        );

        verify(impulseRepository)
                .update(impulse);
    }

    @Test
    void shouldThrowWhenImpulseDoesNotExist() {
        UUID id = UUID.randomUUID();

        when(impulseRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(
                ImpulseNotFoundException.class,
                () -> service.purchase(id)
        );

        verify(impulseRepository, never())
                .update(any());
    }

    @Test
    void shouldRejectPurchasingSkippedImpulse() {
        UUID id = UUID.randomUUID();

        Impulse impulse =
                createImpulse(id);

        impulse.skip();

        when(impulseRepository.findById(id))
                .thenReturn(Optional.of(impulse));

        assertThrows(
                InvalidImpulseStateException.class,
                () -> service.purchase(id)
        );

        verify(impulseRepository, never())
                .update(any());
    }

    private Impulse createImpulse(UUID id) {
        return Impulse.quarantine(
                id,
                "MacBook Pro M5",
                new Money(
                        new BigDecimal("89999.90"),
                        Currency.getInstance("TRY")
                ),
                CREATED_AT
        );
    }
}