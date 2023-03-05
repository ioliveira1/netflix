package com.ioliveira.catalogo.application.genre.create;

import com.ioliveira.catalogo.domain.category.CategoryGateway;
import com.ioliveira.catalogo.domain.category.CategoryID;
import com.ioliveira.catalogo.domain.exceptions.NotificationException;
import com.ioliveira.catalogo.domain.genre.Genre;
import com.ioliveira.catalogo.domain.genre.GenreGateway;
import com.ioliveira.catalogo.domain.validation.Error;
import com.ioliveira.catalogo.domain.validation.ValidationHandler;
import com.ioliveira.catalogo.domain.validation.handler.Notification;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class DefaultCreateGenreUseCase extends CreateGenreUseCase {

    private final CategoryGateway categoryGateway;
    private final GenreGateway genreGateway;

    public DefaultCreateGenreUseCase(final CategoryGateway categoryGateway,
                                     final GenreGateway genreGateway) {
        this.categoryGateway = Objects.requireNonNull(categoryGateway);
        this.genreGateway = Objects.requireNonNull(genreGateway);
    }

    @Override
    public CreateGenreOutput execute(final CreateGenreCommand createGenreCommand) {
        final List<CategoryID> categories = createGenreCommand.categories().stream()
                .map(CategoryID::from).toList();

        final Notification notification = Notification.create();

        validateCategories(categories, notification);

        final Genre genre = notification
                .validate(() -> Genre.newGenre(createGenreCommand.name(), createGenreCommand.active()));

        if (notification.hasErrors()) {
            throw new NotificationException(notification);
        }

        genre.addCategories(categories);

        return CreateGenreOutput.from(this.genreGateway.create(genre));
    }

    private void validateCategories(final List<CategoryID> categories, ValidationHandler handler) {
        if (categories == null || categories.isEmpty()) {
            return;
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

    }
}
