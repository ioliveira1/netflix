package com.ioliveira.catalogo.application.genre.retrieve.get;

import com.ioliveira.catalogo.domain.category.CategoryID;
import com.ioliveira.catalogo.domain.genre.Genre;

import java.time.Instant;
import java.util.List;

public record GenreOutput(String id,
                          String name,
                          boolean active,
                          List<String> categories,
                          Instant createdAt,
                          Instant updatedAt,
                          Instant deletedAt
) {

    public static GenreOutput from(final Genre genre) {
        return new GenreOutput(
                genre.getId().getValue(),
                genre.getName(),
                genre.isActive(),
                genre.getCategories().stream().map(CategoryID::getValue).toList(),
                genre.getCreatedAt(),
                genre.getUpdatedAt(),
                genre.getDeletedAt()
        );
    }
}
