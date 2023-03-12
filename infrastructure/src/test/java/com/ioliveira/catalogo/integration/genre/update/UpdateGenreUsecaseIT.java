package com.ioliveira.catalogo.integration.genre.update;

import com.ioliveira.catalogo.IntegrationTest;
import com.ioliveira.catalogo.application.genre.update.UpdateGenreCommand;
import com.ioliveira.catalogo.application.genre.update.UpdateGenreUseCase;
import com.ioliveira.catalogo.domain.category.CategoryID;
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
public class UpdateGenreUsecaseIT {

    @Autowired
    private UpdateGenreUseCase useCase;

    @Autowired
    private GenreRepository genreRepository;

    @BeforeEach
    void cleanUp() {
        genreRepository.deleteAll();
    }

    @Test
    public void givenAValidCommand_whenCallsUpdateGenre_shouldReturnGenreId() {
        final Genre genre = Genre.newGenre("accao", true);

        final var expectegId = genre.getId();
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.<CategoryID>of();

        assertEquals(0, genreRepository.count());

        genreRepository.saveAndFlush(GenreJpaEntity.from(genre));

        assertEquals(1, genreRepository.count());

        final var command = UpdateGenreCommand.with(
                expectegId.getValue(),
                expectedName,
                expectedIsActive,
                asString(expectedCategories)
        );

        final var output = useCase.execute(command);

        assertNotNull(output);
        assertEquals(expectegId.getValue(), output.id());

        final var genreUpdatedDB = genreRepository.findById(genre.getId().getValue()).get();

        assertNotNull(output);
        assertEquals(expectedName, genreUpdatedDB.getName());
    }

    private List<String> asString(final List<CategoryID> list) {
        return list.stream()
                .map(CategoryID::getValue)
                .toList();
    }
}
