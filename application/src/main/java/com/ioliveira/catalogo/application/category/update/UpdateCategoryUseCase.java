package com.ioliveira.catalogo.application.category.update;

import com.ioliveira.catalogo.application.UseCase;
import com.ioliveira.catalogo.domain.validation.handler.Notification;
import io.vavr.control.Either;

public abstract class UpdateCategoryUseCase extends UseCase<UpdateCategoryCommand, Either<Notification, UpdateCategoryOutput>> {
}
