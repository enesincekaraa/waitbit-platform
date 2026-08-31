package com.waitbit.impulse.application;

import com.waitbit.impulse.domain.Impulse;

import java.util.Objects;
import java.util.UUID;

public final class FindImpulseService {

    private final ImpulseRepository impulseRepository;

    public FindImpulseService(
            ImpulseRepository impulseRepository
    ) {
        this.impulseRepository =
                Objects.requireNonNull(
                        impulseRepository,
                        "impulseRepository must not be null"
                );
    }


    public Impulse findById(UUID id) {
        Objects.requireNonNull(
                id, "id must not be null"
        );

        return impulseRepository.findById(id)
                .orElseThrow(
                        () -> new ImpulseNotFoundException(id)
                );

    }
}
