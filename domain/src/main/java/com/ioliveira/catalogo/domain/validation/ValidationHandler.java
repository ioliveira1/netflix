package com.ioliveira.catalogo.domain.validation;

import java.util.List;

public interface ValidationHandler {

    ValidationHandler append(Error error);

    ValidationHandler append(ValidationHandler handler);

    ValidationHandler validate(Validation validation);

    default boolean hasErrors() {
        return getErrors() != null && !getErrors().isEmpty();
    }

    List<Error> getErrors();
}
