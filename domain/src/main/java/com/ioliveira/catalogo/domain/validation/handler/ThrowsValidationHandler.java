package com.ioliveira.catalogo.domain.validation.handler;

import com.ioliveira.catalogo.domain.exceptions.DomainException;
import com.ioliveira.catalogo.domain.validation.Error;
import com.ioliveira.catalogo.domain.validation.Validation;
import com.ioliveira.catalogo.domain.validation.ValidationHandler;

import java.util.List;

public class ThrowsValidationHandler implements ValidationHandler {
    @Override
    public ValidationHandler append(final Error error) {
        throw DomainException.with(error);
    }

    @Override
    public ValidationHandler append(final ValidationHandler handler) {
        throw DomainException.with(handler.getErrors());
    }

    @Override
    public <T> T validate(final Validation<T> validation) {
        try {
            return validation.validate();
        } catch (final Exception e) {
            throw DomainException.with(new Error(e.getMessage()));
        }
    }

    @Override
    public List<Error> getErrors() {
        return List.of();
    }
}
