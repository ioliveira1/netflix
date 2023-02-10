package com.ioliveira.catalogo.application.category.update;

import com.ioliveira.catalogo.domain.category.Category;
import com.ioliveira.catalogo.domain.category.CategoryGateway;
import com.ioliveira.catalogo.domain.category.CategoryID;
import com.ioliveira.catalogo.domain.exceptions.DomainException;
import com.ioliveira.catalogo.domain.validation.Error;
import com.ioliveira.catalogo.domain.validation.handler.Notification;
import io.vavr.API;
import io.vavr.control.Either;

import java.util.Objects;

import static io.vavr.API.Left;

public class DefaultUpdateCategoryUseCase extends UpdateCategoryUseCase {

    private final CategoryGateway categoryGateway;

    public DefaultUpdateCategoryUseCase(final CategoryGateway categoryGateway) {
        this.categoryGateway = Objects.requireNonNull(categoryGateway);
    }

    @Override
    public Either<Notification, UpdateCategoryOutput> execute(final UpdateCategoryCommand command) {

        final Notification notification = Notification.create();
        final CategoryID id = CategoryID.from(command.id());

        final Category category = categoryGateway
                .findById(id)
                .orElseThrow(() -> DomainException.with(new Error("Category with ID %s not found".formatted(id.getValue()))));

        category.update(command.name(), command.description(), command.isActive()).validate(notification);

        return notification.hasErrors() ? Left(notification) : update(category);
    }

    private Either<Notification, UpdateCategoryOutput> update(final Category category) {
        return API.Try(() -> this.categoryGateway.update(category))
                .toEither()
                .bimap(Notification::create, UpdateCategoryOutput::from);
    }
}
