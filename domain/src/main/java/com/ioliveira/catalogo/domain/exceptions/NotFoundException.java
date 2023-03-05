package com.ioliveira.catalogo.domain.exceptions;

import com.ioliveira.catalogo.domain.AggregateRoot;
import com.ioliveira.catalogo.domain.validation.Error;

import java.util.List;

public class NotFoundException extends DomainException {

    protected NotFoundException(final List<Error> errors) {
        super(errors);
    }

    public static NotFoundException with(Error errors) {
        return new NotFoundException(List.of(errors));
    }

    public static NotFoundException with(final Class<? extends AggregateRoot<?>> aggregate, String id) {
        final String message = "%s ID %s was not found".formatted(aggregate.getSimpleName(), id);
        return NotFoundException.with(new Error(message));
    }
}
