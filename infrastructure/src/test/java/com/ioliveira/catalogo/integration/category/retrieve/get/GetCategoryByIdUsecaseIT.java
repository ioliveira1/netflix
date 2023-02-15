package com.ioliveira.catalogo.integration.category.retrieve.get;

import com.ioliveira.catalogo.IntegrationTest;
import com.ioliveira.catalogo.application.category.retrieve.get.GetCategoryByIdUsecase;
import com.ioliveira.catalogo.domain.category.Category;
import com.ioliveira.catalogo.infrastructure.category.persistence.CategoryJpaEntity;
import com.ioliveira.catalogo.infrastructure.category.persistence.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@IntegrationTest
public class GetCategoryByIdUsecaseIT {

    @Autowired
    private GetCategoryByIdUsecase useCase;

    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeEach
    public void cleanUp() {
        categoryRepository.deleteAll();
    }

    @Test
    public void givenAValidId_whenCallsGetCategory_thenShouldReturnCategory() {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        assertEquals(0, categoryRepository.count());

        final var category = Category.newCategory(expectedName, expectedDescription, expectedIsActive);
        final var expectedId = category.getId();

        categoryRepository.saveAndFlush(CategoryJpaEntity.from(category));

        assertEquals(1, categoryRepository.count());

        final var output = useCase.execute(expectedId.getValue());

        assertNotNull(output);
        assertNotNull(output.id());
        assertEquals(expectedId, output.id());
        assertEquals(expectedName, output.name());
    }
}
