package com.ioliveira.catalogo.domain.genre;

import com.ioliveira.catalogo.domain.pagination.Pagination;
import com.ioliveira.catalogo.domain.pagination.SearchQuery;

import java.util.Optional;

public interface GenreGateway {

    Genre create(Genre genre);

    void deleteById(GenreID id);

    Optional<Genre> findById(GenreID id);

    Genre update(Genre aGenre);

    Pagination<Genre> findAll(SearchQuery query);
}
