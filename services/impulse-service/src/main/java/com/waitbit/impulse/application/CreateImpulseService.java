package com.waitbit.impulse.application;

import com.waitbit.impulse.domain.Impulse;
import com.waitbit.impulse.domain.Money;

import java.time.Clock;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public final class CreateImpulseService {

    private final ImpulseRepository impulseRepository;
    private final Clock clock;

    public CreateImpulseService(
            ImpulseRepository impulseRepository,
            Clock clock
    ) {
        this.impulseRepository = Objects.requireNonNull(
                impulseRepository,
                "impulseRepository must not be null"
        );

        this.clock = Objects.requireNonNull(
                clock,
                "clock must not be null"
        );
    }

    public Impulse create(CreateImpulseCommand command){
        Objects.requireNonNull(command, "command must not be null");

        Money price = new Money(
                command.amount(),
                command.currency()
        );

        Impulse impulse = Impulse.quarantine(
                UUID.randomUUID(),
                command.productName(),
                price,
                Instant.now(clock)
        );
        return impulseRepository.save(impulse);
    }
}
