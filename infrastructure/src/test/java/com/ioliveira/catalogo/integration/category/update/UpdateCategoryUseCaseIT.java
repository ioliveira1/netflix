package com.ioliveira.catalogo.integration.category.update;

import com.ioliveira.catalogo.IntegrationTest;
import com.ioliveira.catalogo.application.category.update.UpdateCategoryCommand;
import com.ioliveira.catalogo.application.category.update.UpdateCategoryUseCase;
import com.ioliveira.catalogo.domain.category.Category;
import com.ioliveira.catalogo.infrastructure.category.persistence.CategoryJpaEntity;
import com.ioliveira.catalogo.infrastructure.category.persistence.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@IntegrationTest
public class UpdateCategoryUseCaseIT {

    @Autowired
    private UpdateCategoryUseCase useCase;

    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeEach
    public void cleanUp() {
        categoryRepository.deleteAll();
    }

    @Test
    public void givenAValidCommand_whenCallsUpdateCategory_thenShouldReturnCategoryId() {
        assertEquals(0, categoryRepository.count());

        final var category = Category.newCategory("Film", null, true);

        categoryRepository.saveAndFlush(CategoryJpaEntity.from(category));

        assertEquals(1, categoryRepository.count());

        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var expectedId = category.getId();
        final var command = UpdateCategoryCommand.with(expectedId.getValue(), expectedName, expectedDescription, expectedIsActive);

        final var output = useCase.execute(command).get();

        assertNotNull(output);
        assertEquals(expectedId.getValue(), output.id());

        final CategoryJpaEntity categoryDB = categoryRepository.findById(output.id()).get();

        assertEquals(expectedName, categoryDB.getName());
        assertEquals(expectedDescription, categoryDB.getDescription());
        assertEquals(expectedIsActive, categoryDB.getActive());
        assertEquals(expectedIsActive, categoryDB.getActive());
        assertTrue(category.getUpdatedAt().isBefore(categoryDB.getUpdatedAt()));
    }
}
