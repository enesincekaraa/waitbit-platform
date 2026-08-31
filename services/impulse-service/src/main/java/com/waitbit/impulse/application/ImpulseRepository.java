package com.waitbit.impulse.application;

import com.waitbit.impulse.domain.Impulse;

public interface ImpulseRepository {
    Impulse save(Impulse impulse);
}
