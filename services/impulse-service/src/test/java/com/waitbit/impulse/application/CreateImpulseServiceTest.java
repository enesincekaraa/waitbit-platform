package com.waitbit.impulse.application;

import com.waitbit.impulse.domain.Impulse;
import com.waitbit.impulse.domain.ImpulseStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateImpulseServiceTest {

    private static final Instant NOW =
            Instant.parse("2026-08-30T17:00:00Z");

    private static final Clock CLOCK =
            Clock.fixed(NOW, ZoneOffset.UTC);

    @Mock
    private ImpulseRepository impulseRepository;

    private CreateImpulseService service;

    @BeforeEach
    void setUp() {
        service = new CreateImpulseService(
                impulseRepository,
                CLOCK
        );
    }

    @Test
    void shouldCreateAndSaveQuarantinedImpulse() {

        CreateImpulseCommand command =
                new CreateImpulseCommand(
                        "Sony WH-1000XM6",
                        new BigDecimal("19499.90"),
                        Currency.getInstance("TRY")
                );

        when(impulseRepository.save(any(Impulse.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Impulse created = service.create(command);

        assertNotNull(created.id());

        assertEquals(
                "Sony WH-1000XM6",
                created.productName()
        );

        assertEquals(
                ImpulseStatus.QUARANTINED,
                created.status()
        );

        assertEquals(
                NOW,
                created.createdAt()
        );

        ArgumentCaptor<Impulse> impulseCaptor =
                ArgumentCaptor.forClass(Impulse.class);

        verify(impulseRepository, times(1))
                .save(impulseCaptor.capture());

        Impulse savedImpulse =
                impulseCaptor.getValue();

        assertEquals(
                "Sony WH-1000XM6",
                savedImpulse.productName()
        );

        assertEquals(
                new BigDecimal("19499.90"),
                savedImpulse.price().amount()
        );

        assertEquals(
                Currency.getInstance("TRY"),
                savedImpulse.price().currency()
        );

        assertEquals(
                ImpulseStatus.QUARANTINED,
                savedImpulse.status()
        );

        assertEquals(
                NOW,
                savedImpulse.createdAt()
        );
    }
}