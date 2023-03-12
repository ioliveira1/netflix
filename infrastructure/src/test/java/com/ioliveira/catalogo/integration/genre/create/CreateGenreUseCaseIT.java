package com.ioliveira.catalogo.integration.genre.create;

import com.ioliveira.catalogo.IntegrationTest;
import com.ioliveira.catalogo.application.genre.create.CreateGenreCommand;
import com.ioliveira.catalogo.application.genre.create.CreateGenreUseCase;
import com.ioliveira.catalogo.domain.category.Category;
import com.ioliveira.catalogo.domain.category.CategoryGateway;
import com.ioliveira.catalogo.domain.category.CategoryID;
import com.ioliveira.catalogo.domain.genre.GenreGateway;
import com.ioliveira.catalogo.infrastructure.category.persistence.CategoryJpaEntity;
import com.ioliveira.catalogo.infrastructure.category.persistence.CategoryRepository;
import com.ioliveira.catalogo.infrastructure.genre.persistence.GenreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@IntegrationTest
public class CreateGenreUseCaseIT {

    @Autowired
    private CreateGenreUseCase useCase;

    @SpyBean
    private GenreGateway genreGateway;

    @SpyBean
    private CategoryGateway categoryGateway;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeEach
    public void cleanUp() {
        this.genreRepository.deleteAll();
        this.categoryRepository.deleteAll();
    }

    @Test
    public void givenAValidCommand_whenCallsCreateGenre_shouldReturnGenreId() {
        final var filmes =
                Category.newCategory("Filmes", "A categoria mais assistida", true);

        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.of(filmes.getId());

        assertEquals(0, categoryRepository.count());

        categoryRepository.saveAndFlush(CategoryJpaEntity.from(filmes));

        assertEquals(1, categoryRepository.count());

        final var command =
                CreateGenreCommand.with(expectedName, expectedIsActive, asString(expectedCategories));

        final var output = useCase.execute(command);

        assertNotNull(output);
        assertNotNull(output.id());

        final var genreDB = genreRepository.findById(output.id()).get();

        assertEquals(expectedName, genreDB.getName());
        assertEquals(expectedIsActive, genreDB.isActive());
        assertTrue(
                expectedCategories.size() == genreDB.getCategoryIDs().size()
                        && expectedCategories.containsAll(genreDB.getCategoryIDs())
        );
        assertNotNull(genreDB.getCreatedAt());
        assertNotNull(genreDB.getUpdatedAt());
        assertNull(genreDB.getDeletedAt());
    }

    private List<String> asString(final List<CategoryID> list) {
        return list.stream()
                .map(CategoryID::getValue)
                .toList();
    }

}
