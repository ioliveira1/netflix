package com.ioliveira.catalogo.infrastructure.genre.presenters;

import com.ioliveira.catalogo.application.genre.retrieve.get.GenreOutput;
import com.ioliveira.catalogo.application.genre.retrieve.list.GenreListOutput;
import com.ioliveira.catalogo.infrastructure.genre.models.GenreListResponse;
import com.ioliveira.catalogo.infrastructure.genre.models.GenreResponse;

public interface GenreApiPresenter {

    static GenreResponse present(final GenreOutput output) {
        return new GenreResponse(
                output.id(),
                output.name(),
                output.categories(),
                output.active(),
                output.createdAt(),
                output.updatedAt(),
                output.deletedAt()
        );
    }

    static GenreListResponse present(final GenreListOutput output) {
        return new GenreListResponse(
                output.id(),
                output.name(),
                output.active(),
                output.createdAt(),
                output.deletedAt()
        );
    }
}