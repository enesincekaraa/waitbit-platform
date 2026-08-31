package com.waitbit.impulse.web;

import com.waitbit.impulse.application.*;
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
    private final SkipImpulseService skipImpulseService;
    private final PurchaseImpulseService  purchaseImpulseService;

    public ImpulseController(
            CreateImpulseService createImpulseService, FindImpulseService findImpulseService, SkipImpulseService skipImpulseService, PurchaseImpulseService purchaseImpulseService
    ) {
        this.createImpulseService = createImpulseService;
        this.findImpulseService = findImpulseService;
        this.skipImpulseService = skipImpulseService;
        this.purchaseImpulseService = purchaseImpulseService;
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
    @PostMapping("/{id}/skip")
    public ImpulseResponse skip(
            @PathVariable UUID id
    ) {
        Impulse skipped =
                skipImpulseService.skip(id);

        return ImpulseResponse.from(skipped);
    }

    @PostMapping("/{id}/purchase")
    public ImpulseResponse purchase(
            @PathVariable UUID id
    ) {
        Impulse purchased =
                purchaseImpulseService.purchase(id);

        return ImpulseResponse.from(purchased);
    }



    private Currency parseCurrency(String currencyCode) {
        try {
            return Currency.getInstance(currencyCode.toUpperCase());
        }catch (IllegalArgumentException e) {
            throw new InvalidCurrencyCodeException(currencyCode);
        }

    }
}