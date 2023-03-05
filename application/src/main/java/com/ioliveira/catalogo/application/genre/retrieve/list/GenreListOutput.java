package com.ioliveira.catalogo.application.genre.retrieve.list;

import com.ioliveira.catalogo.domain.category.CategoryID;
import com.ioliveira.catalogo.domain.genre.Genre;

import java.time.Instant;
import java.util.List;

public record GenreListOutput(
        String name,
        boolean active,
        List<String> categories,
        Instant createdAt,
        Instant deletedAt
) {

    public static GenreListOutput from(final Genre genre) {
        return new GenreListOutput(
                genre.getName(),
                genre.isActive(),
                genre.getCategories().stream().map(CategoryID::getValue).toList(),
                genre.getCreatedAt(),
                genre.getDeletedAt());
    }

}
