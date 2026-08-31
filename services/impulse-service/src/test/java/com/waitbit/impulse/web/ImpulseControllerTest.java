package com.waitbit.impulse.web;

import com.waitbit.impulse.application.CreateImpulseCommand;
import com.waitbit.impulse.application.CreateImpulseService;
import com.waitbit.impulse.domain.Impulse;
import com.waitbit.impulse.domain.Money;
import com.waitbit.impulse.web.error.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Currency;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ImpulseController.class)
@Import(GlobalExceptionHandler.class)
class ImpulseControllerTest {

    private static final UUID IMPULSE_ID =
            UUID.fromString(
                    "f3741ebd-2524-4b76-b2ee-a0a58dc80ad1"
            );

    private static final Instant CREATED_AT =
            Instant.parse("2026-08-31T12:30:22Z");

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateImpulseService createImpulseService;

    @Test
    void shouldCreateImpulse() throws Exception {

        Impulse impulse = Impulse.quarantine(
                IMPULSE_ID,
                "Sony WH-1000XM6",
                new Money(
                        new BigDecimal("19499.90"),
                        Currency.getInstance("TRY")
                ),
                CREATED_AT
        );

        when(createImpulseService.create(
                any(CreateImpulseCommand.class)
        )).thenReturn(impulse);

        mockMvc.perform(
                        post("/api/v1/impulses")
                                .contentType("application/json")
                                .content("""
                                        {
                                          "productName": "Sony WH-1000XM6",
                                          "amount": 19499.90,
                                          "currency": "TRY"
                                        }
                                        """)
                )
                .andExpect(status().isCreated())
                .andExpect(header().string(
                        "Location",
                        "/api/v1/impulses/" + IMPULSE_ID
                ))
                .andExpect(jsonPath("$.id")
                        .value(IMPULSE_ID.toString()))
                .andExpect(jsonPath("$.productName")
                        .value("Sony WH-1000XM6"))
                .andExpect(jsonPath("$.amount")
                        .value(19499.90))
                .andExpect(jsonPath("$.currency")
                        .value("TRY"))
                .andExpect(jsonPath("$.status")
                        .value("QUARANTINED"))
                .andExpect(jsonPath("$.createdAt")
                        .value("2026-08-31T12:30:22Z"))
                .andExpect(jsonPath("$.quarantineEndsAt")
                        .value("2026-09-07T12:30:22Z"));

        verify(createImpulseService)
                .create(any(CreateImpulseCommand.class));
    }

    @Test
    void shouldRejectInvalidRequest() throws Exception {

        mockMvc.perform(
                        post("/api/v1/impulses")
                                .contentType("application/json")
                                .content("""
                                    {
                                      "productName": "   ",
                                      "amount": -500,
                                      "currency": ""
                                    }
                                    """)
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(
                        "application/problem+json"
                ))
                .andExpect(jsonPath("$.code")
                        .value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.title")
                        .value("Validation failed"))
                .andExpect(jsonPath("$.errors")
                        .isArray());
    }

    @Test
    void shouldRejectInvalidCurrency() throws Exception {

        mockMvc.perform(
                        post("/api/v1/impulses")
                                .contentType("application/json")
                                .content("""
                                    {
                                      "productName": "Sony WH-1000XM6",
                                      "amount": 19499.90,
                                      "currency": "ABC"
                                    }
                                    """)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code")
                        .value("INVALID_CURRENCY"))
                .andExpect(jsonPath("$.title")
                        .value("Invalid currency"))
                .andExpect(jsonPath("$.detail")
                        .value(
                                "Unsupported currency code: ABC"
                        ));
    }
}