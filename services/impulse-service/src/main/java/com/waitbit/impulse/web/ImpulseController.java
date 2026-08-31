package com.waitbit.impulse.web;

import com.waitbit.impulse.application.CreateImpulseCommand;
import com.waitbit.impulse.application.CreateImpulseService;
import com.waitbit.impulse.domain.Impulse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Currency;

@RestController
@RequestMapping("/api/v1/impulses")
public class ImpulseController {

    private final CreateImpulseService createImpulseService;

    public ImpulseController(
            CreateImpulseService createImpulseService
    ) {
        this.createImpulseService = createImpulseService;
    }

    @PostMapping
    public ResponseEntity<CreateImpulseResponse> create(
            @Valid @RequestBody CreateImpulseRequest request
    ) {

        CreateImpulseCommand command =
                new CreateImpulseCommand(
                        request.productName(),
                        request.amount(),
                        parseCurrency(request.currency())
                );

        Impulse created =
                createImpulseService.create(command);

        CreateImpulseResponse response =
                CreateImpulseResponse.from(created);

        URI location =
                URI.create("/api/v1/impulses/" + created.id());

        return ResponseEntity
                .created(location)
                .body(response);
    }



    private Currency parseCurrency(String currencyCode) {
        try {
            return Currency.getInstance(currencyCode.toUpperCase());
        }catch (IllegalArgumentException e) {
            throw new InvalidCurrencyCodeException(currencyCode);
        }

    }
}