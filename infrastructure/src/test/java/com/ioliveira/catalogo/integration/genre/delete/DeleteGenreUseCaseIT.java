package com.ioliveira.catalogo.integration.genre.delete;

import com.ioliveira.catalogo.IntegrationTest;
import com.ioliveira.catalogo.application.genre.delete.DeleteGenreUseCase;
import com.ioliveira.catalogo.domain.genre.Genre;
import com.ioliveira.catalogo.infrastructure.genre.persistence.GenreJpaEntity;
import com.ioliveira.catalogo.infrastructure.genre.persistence.GenreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@IntegrationTest
public class DeleteGenreUseCaseIT {

    @Autowired
    private DeleteGenreUseCase useCase;

    @Autowired
    private GenreRepository genreRepository;

    @BeforeEach
    public void cleanUp() {
        genreRepository.deleteAll();
    }

    @Test
    public void givenAValidId_whenCallsDeleteCategory_thenShouldDeleteCategory() {
        final var acao = Genre.newGenre("Ação", true);
        final var drama = Genre.newGenre("Drama", true);
        final var terror = Genre.newGenre("Terror", true);

        final var expectedId = drama.getId();

        final var genres = List.of(
                GenreJpaEntity.from(acao),
                GenreJpaEntity.from(drama),
                GenreJpaEntity.from(terror)
        );

        assertEquals(0, genreRepository.count());

        genreRepository.saveAllAndFlush(genres);

        assertEquals(3, genreRepository.count());

        useCase.execute(expectedId.getValue());

        assertEquals(2, genreRepository.count());
    }
}
