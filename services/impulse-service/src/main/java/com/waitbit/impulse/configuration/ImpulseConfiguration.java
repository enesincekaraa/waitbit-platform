package com.waitbit.impulse.configuration;

import com.waitbit.impulse.application.CreateImpulseService;
import com.waitbit.impulse.application.FindImpulseService;
import com.waitbit.impulse.application.ImpulseRepository;
import com.waitbit.impulse.infrastructure.persistence.InMemoryImpulseRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class ImpulseConfiguration {

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    public ImpulseRepository impulseRepository() {
        return new InMemoryImpulseRepository();
    }

    @Bean
    public CreateImpulseService createImpulseService(ImpulseRepository impulseRepository, Clock clock) {
        return new CreateImpulseService(impulseRepository, clock);
    }

    @Bean
    public FindImpulseService  findImpulseService(ImpulseRepository impulseRepository) {
        return new FindImpulseService(impulseRepository);
    }



}
