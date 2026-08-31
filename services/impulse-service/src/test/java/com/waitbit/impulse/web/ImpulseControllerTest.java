package com.waitbit.impulse.web;

import com.waitbit.impulse.application.*;
import com.waitbit.impulse.domain.Impulse;
import com.waitbit.impulse.domain.InvalidImpulseStateException;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    @MockitoBean
    private FindImpulseService findImpulseService;

    @MockitoBean
    private SkipImpulseService skipImpulseService;

    @MockitoBean
    private PurchaseImpulseService purchaseImpulseService;

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


    @Test
    void shouldFindImpulseById() throws Exception {

        Impulse impulse = Impulse.quarantine(
                IMPULSE_ID,
                "Sony WH-1000XM6",
                new Money(
                        new BigDecimal("19499.90"),
                        Currency.getInstance("TRY")
                ),
                CREATED_AT
        );

        when(findImpulseService.findById(IMPULSE_ID))
                .thenReturn(impulse);

        mockMvc.perform(
                        get("/api/v1/impulses/{id}", IMPULSE_ID)
                )
                .andExpect(status().isOk())
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

        verify(findImpulseService)
                .findById(IMPULSE_ID);
    }

    @Test
    void shouldReturnNotFoundWhenImpulseDoesNotExist()
            throws Exception {

        UUID missingId =
                UUID.fromString(
                        "11111111-1111-1111-1111-111111111111"
                );

        when(findImpulseService.findById(missingId))
                .thenThrow(
                        new ImpulseNotFoundException(missingId)
                );

        mockMvc.perform(
                        get("/api/v1/impulses/{id}", missingId)
                )
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(
                        "application/problem+json"
                ))
                .andExpect(jsonPath("$.status")
                        .value(404))
                .andExpect(jsonPath("$.title")
                        .value("Impulse not found"))
                .andExpect(jsonPath("$.code")
                        .value("IMPULSE_NOT_FOUND"))
                .andExpect(jsonPath("$.detail")
                        .value(
                                "Impulse not found with id: "
                                        + missingId
                        ));

        verify(findImpulseService)
                .findById(missingId);
    }


    @Test
    void shouldSkipImpulse() throws Exception {
        Impulse impulse = Impulse.quarantine(
                IMPULSE_ID,
                "PlayStation 5 Pro",
                new Money(
                        new BigDecimal("38999.90"),
                        Currency.getInstance("TRY")
                ),
                CREATED_AT
        );

        impulse.skip();

        when(skipImpulseService.skip(IMPULSE_ID))
                .thenReturn(impulse);


        mockMvc.perform(
                        post("/api/v1/impulses/{id}/skip", IMPULSE_ID)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value(IMPULSE_ID.toString()))
                .andExpect(jsonPath("$.productName")
                        .value("PlayStation 5 Pro"))
                .andExpect(jsonPath("$.status")
                        .value("SKIPPED"));

        verify(skipImpulseService)
                .skip(IMPULSE_ID);
    }

    @Test
    void shouldReturnConflictWhenImpulseCannotBeSkipped()
            throws Exception {

        when(skipImpulseService.skip(IMPULSE_ID))
                .thenThrow(
                        new InvalidImpulseStateException(
                                "Impulse cannot be skipped from status: SKIPPED"
                        )
                );

        mockMvc.perform(
                        post("/api/v1/impulses/{id}/skip", IMPULSE_ID)
                )
                .andExpect(status().isConflict())
                .andExpect(content().contentType(
                        "application/problem+json"
                ))
                .andExpect(jsonPath("$.status")
                        .value(409))
                .andExpect(jsonPath("$.title")
                        .value("Invalid impulse state"))
                .andExpect(jsonPath("$.code")
                        .value("INVALID_IMPULSE_STATE"))
                .andExpect(jsonPath("$.detail")
                        .value(
                                "Impulse cannot be skipped from status: SKIPPED"
                        ));
    }

    @Test
    void shouldReturnNotFoundWhenSkippingMissingImpulse()
            throws Exception {

        UUID missingId =
                UUID.fromString(
                        "11111111-1111-1111-1111-111111111111"
                );

        when(skipImpulseService.skip(missingId))
                .thenThrow(
                        new ImpulseNotFoundException(missingId)
                );

        mockMvc.perform(
                        post("/api/v1/impulses/{id}/skip", missingId)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code")
                        .value("IMPULSE_NOT_FOUND"));
    }

    @Test
    void shouldPurchaseImpulse() throws Exception {

        Impulse impulse = Impulse.quarantine(
                IMPULSE_ID,
                "MacBook Pro M5",
                new Money(
                        new BigDecimal("89999.90"),
                        Currency.getInstance("TRY")
                ),
                CREATED_AT
        );

        impulse.purchase();

        when(purchaseImpulseService.purchase(IMPULSE_ID))
                .thenReturn(impulse);

        mockMvc.perform(
                        post(
                                "/api/v1/impulses/{id}/purchase",
                                IMPULSE_ID
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value(IMPULSE_ID.toString()))
                .andExpect(jsonPath("$.productName")
                        .value("MacBook Pro M5"))
                .andExpect(jsonPath("$.amount")
                        .value(89999.90))
                .andExpect(jsonPath("$.currency")
                        .value("TRY"))
                .andExpect(jsonPath("$.status")
                        .value("PURCHASED"));

        verify(purchaseImpulseService)
                .purchase(IMPULSE_ID);
    }

    @Test
    void shouldReturnConflictWhenImpulseCannotBePurchased()
            throws Exception {

        when(purchaseImpulseService.purchase(IMPULSE_ID))
                .thenThrow(
                        new InvalidImpulseStateException(
                                "Impulse cannot be purchased from status: SKIPPED"
                        )
                );

        mockMvc.perform(
                        post(
                                "/api/v1/impulses/{id}/purchase",
                                IMPULSE_ID
                        )
                )
                .andExpect(status().isConflict())
                .andExpect(content().contentType(
                        "application/problem+json"
                ))
                .andExpect(jsonPath("$.status")
                        .value(409))
                .andExpect(jsonPath("$.title")
                        .value("Invalid impulse state"))
                .andExpect(jsonPath("$.code")
                        .value("INVALID_IMPULSE_STATE"))
                .andExpect(jsonPath("$.detail")
                        .value(
                                "Impulse cannot be purchased from status: SKIPPED"
                        ));

        verify(purchaseImpulseService)
                .purchase(IMPULSE_ID);
    }

    @Test
    void shouldReturnNotFoundWhenPurchasingMissingImpulse()
            throws Exception {

        UUID missingId = UUID.fromString(
                "11111111-1111-1111-1111-111111111111"
        );

        when(purchaseImpulseService.purchase(missingId))
                .thenThrow(
                        new ImpulseNotFoundException(missingId)
                );

        mockMvc.perform(
                        post(
                                "/api/v1/impulses/{id}/purchase",
                                missingId
                        )
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status")
                        .value(404))
                .andExpect(jsonPath("$.code")
                        .value("IMPULSE_NOT_FOUND"));

        verify(purchaseImpulseService)
                .purchase(missingId);
    }
}