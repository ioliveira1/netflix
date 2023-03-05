package com.ioliveira.catalogo.application.genre.retrieve.list;

import com.ioliveira.catalogo.application.UseCase;
import com.ioliveira.catalogo.domain.pagination.Pagination;
import com.ioliveira.catalogo.domain.pagination.SearchQuery;

public abstract class ListGenreUseCase extends UseCase<SearchQuery, Pagination<GenreListOutput>> {
}
