package com.ioliveira.catalogo.application.genre.update;

import com.ioliveira.catalogo.domain.category.CategoryGateway;
import com.ioliveira.catalogo.domain.category.CategoryID;
import com.ioliveira.catalogo.domain.exceptions.NotFoundException;
import com.ioliveira.catalogo.domain.exceptions.NotificationException;
import com.ioliveira.catalogo.domain.genre.Genre;
import com.ioliveira.catalogo.domain.genre.GenreGateway;
import com.ioliveira.catalogo.domain.genre.GenreID;
import com.ioliveira.catalogo.domain.validation.Error;
import com.ioliveira.catalogo.domain.validation.ValidationHandler;
import com.ioliveira.catalogo.domain.validation.handler.Notification;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class DefaultUpdateGenreUseCase extends UpdateGenreUseCase {

    private final GenreGateway genreGateway;
    private final CategoryGateway categoryGateway;

    public DefaultUpdateGenreUseCase(final GenreGateway genreGateway,
                                     final CategoryGateway categoryGateway) {
        this.genreGateway = Objects.requireNonNull(genreGateway);
        this.categoryGateway = Objects.requireNonNull(categoryGateway);
    }

    @Override
    public UpdateGenreOutput execute(final UpdateGenreCommand updateGenreCommand) {
        final List<CategoryID> categoryIDS = updateGenreCommand.categories()
                .stream()
                .map(CategoryID::from)
                .toList();

        final Genre genre = this.genreGateway
                .findById(GenreID.from(updateGenreCommand.id()))
                .orElseThrow(() -> NotFoundException.with(
                        new Error("Genre with ID %s not found".formatted(updateGenreCommand.id())))
                );

        final Notification notification = Notification.create();
        validateCategories(categoryIDS, notification);

        notification
                .validate(() -> genre.update(updateGenreCommand.name(), updateGenreCommand.active(), categoryIDS));

        if (notification.hasErrors()) {
            throw new NotificationException(notification);
        }

        final Genre update = this.genreGateway.update(genre);

        return UpdateGenreOutput.from(update);
    }

    private ValidationHandler validateCategories(final List<CategoryID> categories, ValidationHandler handler) {
        if (categories == null || categories.isEmpty()) {
            return handler;
        }

        final List<CategoryID> categoryIDS = categoryGateway.existsByIds(categories);
        if (categoryIDS.size() != categories.size()) {
            final ArrayList<CategoryID> missingIds = new ArrayList<>(categories);

            missingIds.removeAll(categoryIDS);

            final String missingIdsMessage = missingIds.stream()
                    .map(CategoryID::getValue)
                    .collect(Collectors.joining(", "));

            handler.append(new Error("Some categories could not be found: %s".formatted(missingIdsMessage)));
        }

        return handler;
    }
}
