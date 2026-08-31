package com.waitbit.impulse.infrastructure.persistence;

import com.waitbit.impulse.application.ImpulseRepository;
import com.waitbit.impulse.domain.Impulse;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class InMemoryImpulseRepository implements ImpulseRepository {

    private final Map<UUID, Impulse> storage = new ConcurrentHashMap<>();

    @Override
    public Impulse save(Impulse impulse) {
        Objects.requireNonNull(
                impulse,
                "impulse must not be null"
        );

       Impulse existing = storage.putIfAbsent(
                impulse.id(),
                impulse
        );

       if (existing != null) {
           throw new IllegalStateException(
                   "Impulse already exists with id: " + impulse.id()
           );
       }
        return impulse;
    }
}
