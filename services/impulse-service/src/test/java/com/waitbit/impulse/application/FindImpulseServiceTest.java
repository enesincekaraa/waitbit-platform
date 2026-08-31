package com.waitbit.impulse.application;

import com.waitbit.impulse.domain.Impulse;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FindImpulseServiceTest {

    @Mock
    private ImpulseRepository impulseRepository;

    private FindImpulseService service;

    @BeforeEach
    void setUp() {
        service = new FindImpulseService(impulseRepository);
    }


    @Test
    void shouldReturnImpulseWhenItExists() {
        UUID id = UUID.randomUUID();

        Impulse impulse = Impulse.quarantine(
                id,
                "Sony WH-1000XM6",
                new Money(
                        new BigDecimal("19499.90"),
                        Currency.getInstance("TRY")
                ),
                Instant.parse("2026-08-31T12:30:22Z")
        );

        when(impulseRepository.findById(id)).thenReturn(Optional.of(impulse));

        Impulse result = service.findById(id);

        assertSame(impulse, result);


    }


    @Test
    void shouldThrowWhenImpulseDoesNotExist() {
        UUID id = UUID.randomUUID();

        when(impulseRepository.findById(id))
                .thenReturn(Optional.empty());

        ImpulseNotFoundException exception =
                assertThrows(
                        ImpulseNotFoundException.class,
                        () -> service.findById(id)
                );

        assertEquals(
                "Impulse not found with id: " + id,
                exception.getMessage()
        );
    }
}
