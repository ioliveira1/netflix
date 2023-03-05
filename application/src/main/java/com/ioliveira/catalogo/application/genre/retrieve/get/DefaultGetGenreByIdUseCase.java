package com.ioliveira.catalogo.application.genre.retrieve.get;

import com.ioliveira.catalogo.domain.exceptions.NotFoundException;
import com.ioliveira.catalogo.domain.genre.Genre;
import com.ioliveira.catalogo.domain.genre.GenreGateway;
import com.ioliveira.catalogo.domain.genre.GenreID;

import java.util.Objects;

public class DefaultGetGenreByIdUseCase extends GetGenreByIdUseCase {

    private final GenreGateway genreGateway;

    public DefaultGetGenreByIdUseCase(final GenreGateway genreGateway) {
        this.genreGateway = Objects.requireNonNull(genreGateway);
    }

    @Override
    public GenreOutput execute(final String id) {

        return this.genreGateway
                .findById(GenreID.from(id))
                .map(GenreOutput::from)
                .orElseThrow(() -> NotFoundException.with(Genre.class, id));
    }
}
