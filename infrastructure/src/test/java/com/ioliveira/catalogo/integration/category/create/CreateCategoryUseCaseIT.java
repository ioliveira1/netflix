package com.ioliveira.catalogo.integration.category.create;

import com.ioliveira.catalogo.IntegrationTest;
import com.ioliveira.catalogo.application.category.create.CreateCategoryCommand;
import com.ioliveira.catalogo.application.category.create.CreateCategoryUseCase;
import com.ioliveira.catalogo.infrastructure.category.persistence.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@IntegrationTest
public class CreateCategoryUseCaseIT {

    @Autowired
    private CreateCategoryUseCase useCase;

    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeEach
    public void cleanUp() {
        categoryRepository.deleteAll();
    }

    @Test
    public void givenAValidCommand_whenCallsCreateCategory_thenShouldReturnCategoryId() {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        assertEquals(0, categoryRepository.count());

        final var command = CreateCategoryCommand.with(expectedName, expectedDescription, expectedIsActive);

        final var output = useCase.execute(command).get();

        assertEquals(1, categoryRepository.count());

        assertNotNull(output);
        assertNotNull(output.id());

        final var categoryDB = categoryRepository.findById(output.id()).get();

        assertEquals(expectedName, categoryDB.getName());
        assertEquals(expectedDescription, categoryDB.getDescription());
        assertEquals(expectedIsActive, categoryDB.getActive());
        assertNotNull(categoryDB.getCreatedAt());
        assertNotNull(categoryDB.getUpdatedAt());
        assertNull(categoryDB.getDeletedAt());
    }
}
