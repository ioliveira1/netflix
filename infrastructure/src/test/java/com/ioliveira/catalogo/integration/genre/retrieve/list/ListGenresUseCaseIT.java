package com.ioliveira.catalogo.integration.genre.retrieve.list;

import com.ioliveira.catalogo.IntegrationTest;
import com.ioliveira.catalogo.application.genre.retrieve.list.GenreListOutput;
import com.ioliveira.catalogo.application.genre.retrieve.list.ListGenreUseCase;
import com.ioliveira.catalogo.domain.genre.Genre;
import com.ioliveira.catalogo.domain.pagination.SearchQuery;
import com.ioliveira.catalogo.infrastructure.genre.persistence.GenreJpaEntity;
import com.ioliveira.catalogo.infrastructure.genre.persistence.GenreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@IntegrationTest
public class ListGenresUseCaseIT {

    @Autowired
    private ListGenreUseCase useCase;

    @Autowired
    private GenreRepository genreRepository;

    @BeforeEach
    void cleanUp() {
        genreRepository.deleteAll();
    }

    @Test
    public void givenAValidQuery_whenCallsListGenre_shouldReturnGenres() {
        final var acao = Genre.newGenre("Ação", true);
        final var drama = Genre.newGenre("Drama", true);
        final var terror = Genre.newGenre("Terror", true);

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "Am";
        final var expectedSord = "createdAt";
        final var expectedDirection = "asc";
        final var expectedTotal = 1;
        final var expectedItems = List.of(GenreListOutput.from(drama));

        final var genres = List.of(
                GenreJpaEntity.from(acao),
                GenreJpaEntity.from(drama),
                GenreJpaEntity.from(terror)
        );

        assertEquals(0, genreRepository.count());

        genreRepository.saveAllAndFlush(genres);

        assertEquals(3, genreRepository.count());

        final var query = new SearchQuery(
                expectedPage,
                expectedPerPage,
                expectedTerms,
                expectedSord,
                expectedDirection
        );

        final var output = useCase.execute(query);

        assertEquals(expectedTotal, output.total());
        assertEquals(expectedItems, output.items());
    }
}
