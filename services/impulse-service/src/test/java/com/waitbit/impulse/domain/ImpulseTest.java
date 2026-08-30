package com.waitbit.impulse.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Currency;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ImpulseTest {

    private static final Instant CREATED_AT =
            Instant.parse("2026-08-30T12:00:00Z");

    private static final Money PRICE =
            new Money(
                    new BigDecimal("19499.90"),
                    Currency.getInstance("TRY")
            );


    private Impulse createImpulse() {
        return Impulse.quarantine(
                UUID.randomUUID(),
                "Sony WH-1000XM6",
                PRICE,
                CREATED_AT
        );
    }


    @Test
    void shouldCreateImpulseInQuarantinedStatus(){
        Impulse impulse = createImpulse();

        assertEquals(ImpulseStatus.QUARANTINED,impulse.status());

        assertEquals(
                CREATED_AT,
                impulse.createdAt()
        );

        assertEquals(
                CREATED_AT.plus(7, ChronoUnit.DAYS),
                impulse.quarantineEndsAt()
        );
    }

    @Test
    void shouldAllowSkippingDuringQuarantine() {
        Impulse impulse = createImpulse();

        impulse.skip();

        assertEquals(
                ImpulseStatus.SKIPPED,
                impulse.status()
        );
    }


    @Test
    void shouldAllowPurchaseDuringQuarantine() {
        Impulse impulse = createImpulse();

        impulse.purchase();

        assertEquals(
                ImpulseStatus.PURCHASED,
                impulse.status()
        );
    }

    @Test
    void shouldRejectBecomingReadyBeforeQuarantineEnds() {
        Impulse impulse = createImpulse();

        Instant sixthDay =
                CREATED_AT.plus(6, ChronoUnit.DAYS);

        assertThrows(
                IllegalStateException.class,
                () -> impulse.makeReadyForDecision(sixthDay)
        );

        assertEquals(
                ImpulseStatus.QUARANTINED,
                impulse.status()
        );
    }

    @Test
    void shouldBecomeReadyWhenQuarantineEnds() {
        Impulse impulse = createImpulse();

        Instant seventhDay =
                CREATED_AT.plus(7, ChronoUnit.DAYS);

        impulse.makeReadyForDecision(seventhDay);

        assertEquals(
                ImpulseStatus.READY_FOR_DECISION,
                impulse.status()
        );
    }


    @Test
    void shouldRejectPurchaseAfterImpulseWasSkipped() {
        Impulse impulse = createImpulse();

        impulse.skip();

        assertThrows(
                IllegalStateException.class,
                impulse::purchase
        );

        assertEquals(
                ImpulseStatus.SKIPPED,
                impulse.status()
        );
    }

    @Test
    void shouldRejectSkippingAfterImpulseWasPurchased() {
        Impulse impulse = createImpulse();

        impulse.purchase();

        assertThrows(
                IllegalStateException.class,
                impulse::skip
        );

        assertEquals(
                ImpulseStatus.PURCHASED,
                impulse.status()
        );
    }



}
