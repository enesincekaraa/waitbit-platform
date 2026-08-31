package com.waitbit.impulse.application;

import java.util.UUID;

public final class ImpulseNotFoundException
        extends RuntimeException {

    public ImpulseNotFoundException(UUID id) {
        super("Impulse not found with id: " + id);
    }
}