package com.ioliveira.catalogo.application.genre.delete;

import com.ioliveira.catalogo.domain.genre.GenreGateway;
import com.ioliveira.catalogo.domain.genre.GenreID;

import java.util.Objects;

public class DefaultDeleteGenreUseCase extends DeleteGenreUseCase {

    private final GenreGateway genreGateway;

    public DefaultDeleteGenreUseCase(final GenreGateway genreGateway) {
        this.genreGateway = Objects.requireNonNull(genreGateway);
    }


    @Override
    public void execute(final String id) {
        this.genreGateway.deleteById(GenreID.from(id));
    }
}
