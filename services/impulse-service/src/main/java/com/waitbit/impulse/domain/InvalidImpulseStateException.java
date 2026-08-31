package com.waitbit.impulse.domain;

public final class InvalidImpulseStateException extends IllegalStateException {
    public InvalidImpulseStateException(String message) {
        super(message);
    }
}
