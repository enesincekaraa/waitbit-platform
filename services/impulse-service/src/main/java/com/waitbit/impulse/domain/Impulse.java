package com.waitbit.impulse.domain;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public final class Impulse {
    private static final Duration QUARANTINE_DURATION = Duration.ofDays(7);

    private final UUID id;
    private final String productName;
    private final Money price;
    private final Instant createdAt;
    private final Instant quarantineEndsAt;
    private ImpulseStatus status;

    private Impulse(UUID id, String productName, Money price, Instant createdAt) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.price = Objects.requireNonNull(price, "price must not be null");
        this.createdAt = Objects.requireNonNull(
                createdAt,
                "createdAt must not be null"
        );
        if (productName==null || productName.isBlank()) {
            throw new IllegalArgumentException("productName must not be null");
        }
        this.productName = productName;
        this.quarantineEndsAt=createdAt.plus(QUARANTINE_DURATION);
        this.status = ImpulseStatus.QUARANTINED;
    }


    public static Impulse quarantine(UUID id, String productName, Money price, Instant createdAt) {
        return new Impulse(id, productName, price, createdAt);
    }


    public void skip(){
        if (!canBeDecided())throw new InvalidImpulseStateException("Impulse cannot be skipped from status: " + status);
        this.status = ImpulseStatus.SKIPPED;
    }

    public void makeReadyForDecision(Instant now){
        Objects.requireNonNull(now, "now must not be null");

        if (status!=ImpulseStatus.QUARANTINED){
            throw new InvalidImpulseStateException(
                    "Only quarantined impulses can become ready for decision"
            );
        }

        if (now.isBefore(quarantineEndsAt)){
            throw new InvalidImpulseStateException(
                    "Quarantine period has not ended yet"
            );
        }

        this.status = ImpulseStatus.READY_FOR_DECISION;
    }

    public void purchase(){
        if (!canBeDecided())throw new InvalidImpulseStateException("Impulse cannot be purchased from status: " + status);

        this.status = ImpulseStatus.PURCHASED;
    }

    private boolean canBeDecided(){
        return
                status ==ImpulseStatus.QUARANTINED
                        || status == ImpulseStatus.READY_FOR_DECISION;
    }

    public UUID id() {
        return id;
    }

    public String productName() {
        return productName;
    }

    public Money price() {
        return price;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant quarantineEndsAt() {
        return quarantineEndsAt;
    }

    public ImpulseStatus status() {
        return status;
    }


}
