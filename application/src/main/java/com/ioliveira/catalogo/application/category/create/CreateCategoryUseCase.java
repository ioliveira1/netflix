package com.ioliveira.catalogo.application.category.create;

import com.ioliveira.catalogo.application.UseCase;
import com.ioliveira.catalogo.domain.validation.handler.Notification;
import io.vavr.control.Either;

public abstract class CreateCategoryUseCase extends UseCase<CreateCategoryCommand, Either<Notification, CreateCategoryOutput>> {
}
