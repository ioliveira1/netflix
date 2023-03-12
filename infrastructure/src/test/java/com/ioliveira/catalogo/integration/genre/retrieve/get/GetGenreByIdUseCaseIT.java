package com.ioliveira.catalogo.integration.genre.retrieve.get;

import com.ioliveira.catalogo.IntegrationTest;
import com.ioliveira.catalogo.application.genre.retrieve.get.GetGenreByIdUseCase;
import com.ioliveira.catalogo.domain.genre.Genre;
import com.ioliveira.catalogo.infrastructure.genre.persistence.GenreJpaEntity;
import com.ioliveira.catalogo.infrastructure.genre.persistence.GenreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@IntegrationTest
public class GetGenreByIdUseCaseIT {

    @Autowired
    private GetGenreByIdUseCase useCase;

    @Autowired
    private GenreRepository genreRepository;

    @BeforeEach
    public void cleanUp() {
        genreRepository.deleteAll();
    }

    @Test
    public void givenAValidId_whenCallsGetGenreById_shouldReturnGenre() {
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

        final var output = useCase.execute(expectedId.getValue());

        assertNotNull(output);
        assertEquals(expectedId.getValue(), output.id());
    }
}
