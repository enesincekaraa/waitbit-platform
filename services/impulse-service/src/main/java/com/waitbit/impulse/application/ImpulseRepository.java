package com.waitbit.impulse.application;

import com.waitbit.impulse.domain.Impulse;

import java.util.Optional;
import java.util.UUID;

public interface ImpulseRepository {
    Impulse save(Impulse impulse);
    Optional<Impulse> findById(UUID id);
}
