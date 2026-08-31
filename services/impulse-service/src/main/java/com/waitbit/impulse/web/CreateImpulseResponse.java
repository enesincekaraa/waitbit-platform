package com.waitbit.impulse.web;

import com.waitbit.impulse.domain.Impulse;
import com.waitbit.impulse.domain.ImpulseStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record CreateImpulseResponse(
        UUID id,
        String productName,
        BigDecimal amount,
        String currency,
        ImpulseStatus status,
        Instant createdAt,
        Instant quarantineEndsAt
) {

    public static CreateImpulseResponse from(Impulse impulse) {
        return new CreateImpulseResponse(
                impulse.id(),
                impulse.productName(),
                impulse.price().amount(),
                impulse.price().currency().getCurrencyCode(),
                impulse.status(),
                impulse.createdAt(),
                impulse.quarantineEndsAt()
        );
    }
}