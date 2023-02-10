package com.ioliveira.catalogo.application.category.retrieve.get;

import com.ioliveira.catalogo.domain.category.CategoryGateway;
import com.ioliveira.catalogo.domain.category.CategoryID;
import com.ioliveira.catalogo.domain.exceptions.DomainException;
import com.ioliveira.catalogo.domain.validation.Error;

import java.util.Objects;

public class DefaultGetCategoryByIdUsecase extends GetCategoryByIdUsecase {

    private final CategoryGateway categoryGateway;

    public DefaultGetCategoryByIdUsecase(final CategoryGateway categoryGateway) {
        this.categoryGateway = Objects.requireNonNull(categoryGateway);
    }

    @Override
    public GetCategoryByIdOutput execute(final String id) {

        return this.categoryGateway
                .findById(CategoryID.from(id))
                .map(GetCategoryByIdOutput::with)
                .orElseThrow(() -> DomainException.with(new Error("Category with ID %s not found".formatted(id))));
    }
}
