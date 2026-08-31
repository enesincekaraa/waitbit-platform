package com.waitbit.impulse.web;

import com.waitbit.impulse.application.CreateImpulseCommand;
import com.waitbit.impulse.application.CreateImpulseService;
import com.waitbit.impulse.application.FindImpulseService;
import com.waitbit.impulse.domain.Impulse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Currency;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/impulses")
public class ImpulseController {

    private final CreateImpulseService createImpulseService;
    private final FindImpulseService  findImpulseService;

    public ImpulseController(
            CreateImpulseService createImpulseService, FindImpulseService findImpulseService
    ) {
        this.createImpulseService = createImpulseService;
        this.findImpulseService = findImpulseService;
    }

    @PostMapping
    public ResponseEntity<ImpulseResponse> create(
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

        ImpulseResponse response =
                ImpulseResponse.from(created);

        URI location =
                URI.create("/api/v1/impulses/" + created.id());

        return ResponseEntity
                .created(location)
                .body(response);
    }


    @GetMapping("/{id}")
    public ImpulseResponse findById(
            @PathVariable UUID id
    ) {
        Impulse impulse =
                findImpulseService.findById(id);

        return ImpulseResponse.from(impulse);
    }



    private Currency parseCurrency(String currencyCode) {
        try {
            return Currency.getInstance(currencyCode.toUpperCase());
        }catch (IllegalArgumentException e) {
            throw new InvalidCurrencyCodeException(currencyCode);
        }

    }
}