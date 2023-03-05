package com.ioliveira.catalogo.application.category.retrieve.list;

import com.ioliveira.catalogo.application.genre.retrieve.list.GenreListOutput;
import com.ioliveira.catalogo.application.genre.retrieve.list.ListGenreUseCase;
import com.ioliveira.catalogo.domain.genre.GenreGateway;
import com.ioliveira.catalogo.domain.pagination.Pagination;
import com.ioliveira.catalogo.domain.pagination.SearchQuery;

import java.util.Objects;

public class DefaultListGenreUseCase extends ListGenreUseCase {

    private final GenreGateway genreGateway;

    public DefaultListGenreUseCase(final GenreGateway genreGateway) {
        this.genreGateway = Objects.requireNonNull(genreGateway);
    }


    @Override
    public Pagination<GenreListOutput> execute(final SearchQuery query) {
        return this.genreGateway.findAll(query).map(GenreListOutput::from);
    }
}
