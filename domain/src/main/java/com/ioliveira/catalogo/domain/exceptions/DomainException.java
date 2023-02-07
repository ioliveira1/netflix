package com.ioliveira.catalogo.domain.exceptions;

import com.ioliveira.catalogo.domain.validation.Error;

import java.util.List;

public class DomainException extends NoStackTraceException {

    private final List<Error> errors;

    private DomainException(final List<Error> errors) {
        super("");
        this.errors = errors;
    }

    public static DomainException with(Error errors) {
        return new DomainException(List.of(errors));
    }

    public static DomainException with(final List<Error> errors) {
        return new DomainException(errors);
    }

    public List<Error> getErrors() {
        return errors;
    }
}
