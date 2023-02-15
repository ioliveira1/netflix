package com.ioliveira.catalogo.integration.category.delete;

import com.ioliveira.catalogo.IntegrationTest;
import com.ioliveira.catalogo.application.category.delete.DeleteCategoryUseCase;
import com.ioliveira.catalogo.domain.category.Category;
import com.ioliveira.catalogo.infrastructure.category.persistence.CategoryJpaEntity;
import com.ioliveira.catalogo.infrastructure.category.persistence.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;

@IntegrationTest
public class DeleteCategoryUseCaseIT {

    @Autowired
    private DeleteCategoryUseCase useCase;

    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeEach
    public void cleanUp() {
        categoryRepository.deleteAll();
    }

    @Test
    public void givenAValidId_whenCallsDeleteCategory_thenShouldDeleteCategory() {
        final var category = Category.newCategory("Filmes", "A categoria mais assistida", true);

        assertEquals(0, categoryRepository.count());

        categoryRepository.saveAndFlush(CategoryJpaEntity.from(category));

        assertEquals(1, categoryRepository.count());

        useCase.execute(category.getId().getValue());

        assertEquals(0, categoryRepository.count());
    }
}
