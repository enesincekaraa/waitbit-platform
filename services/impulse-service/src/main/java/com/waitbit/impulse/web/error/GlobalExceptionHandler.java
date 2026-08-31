package com.waitbit.impulse.web.error;

import com.waitbit.impulse.application.ImpulseNotFoundException;
import com.waitbit.impulse.web.InvalidCurrencyCodeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidCurrencyCodeException.class)
    public ProblemDetail handleInvalidCurrency(
            InvalidCurrencyCodeException exception
    ) {
        ProblemDetail problem =
                ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);

        problem.setTitle("Invalid currency");
        problem.setDetail(exception.getMessage());
        problem.setProperty(
                "code",
                "INVALID_CURRENCY"
        );

        return problem;
    }

    @ExceptionHandler(ImpulseNotFoundException.class)
    public ProblemDetail handleImpulseNotFound(
            ImpulseNotFoundException exception
    ){
        ProblemDetail problem =
                ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problem.setTitle("Impulse not found");
        problem.setDetail(exception.getMessage());
        problem.setProperty(
                "code",
                "IMPULSE_NOT_FOUND"
        );
        return problem;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(
            MethodArgumentNotValidException exception
    ) {
        ProblemDetail problem =
                ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);

        problem.setTitle("Validation failed");
        problem.setDetail(
                "Request contains invalid fields"
        );
        problem.setProperty(
                "code",
                "VALIDATION_ERROR"
        );
        var errors = exception
                .getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new FieldValidationError(
                        error.getField(),
                        error.getDefaultMessage()
                ))
                .toList();

        problem.setProperty(
                "errors",
                errors
        );

        return problem;
    }

    private record FieldValidationError(
            String field,
            String message
    ) {
    }
}