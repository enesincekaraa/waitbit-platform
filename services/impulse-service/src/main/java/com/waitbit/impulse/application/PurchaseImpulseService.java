package com.waitbit.impulse.application;

import com.waitbit.impulse.domain.Impulse;

import java.util.Objects;
import java.util.UUID;

public final class PurchaseImpulseService {

    private final ImpulseRepository impulseRepository;

    public PurchaseImpulseService(
            ImpulseRepository impulseRepository
    ) {
        this.impulseRepository =
                Objects.requireNonNull(
                        impulseRepository,
                        "impulseRepository must not be null"
                );
    }


    public Impulse purchase(UUID id) {
        Objects.requireNonNull(
                id,
                "id must not be null"
        );

        Impulse impulse = impulseRepository
                .findById(id)
                .orElseThrow(
                        () -> new ImpulseNotFoundException(id)
                );

        impulse.purchase();

        return impulseRepository.update(impulse);
    }
}
