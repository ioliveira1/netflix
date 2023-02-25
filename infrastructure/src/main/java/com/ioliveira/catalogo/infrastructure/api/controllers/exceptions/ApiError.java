package com.ioliveira.catalogo.infrastructure.api.controllers.exceptions;

import com.ioliveira.catalogo.domain.exceptions.DomainException;
import com.ioliveira.catalogo.domain.validation.Error;

import java.util.List;

public record ApiError(String message, List<Error> errors) {

    public static ApiError from(DomainException exception) {
        return new ApiError(exception.getMessage(), exception.getErrors());
    }

}
