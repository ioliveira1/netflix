package com.ioliveira.catalogo.infrastructure.category.persistence;

import com.ioliveira.catalogo.domain.category.Category;
import com.ioliveira.catalogo.MySQLGatewayTest;
import org.hibernate.PropertyValueException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

@MySQLGatewayTest
public class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeEach
    public void cleanUp() {
        this.categoryRepository.deleteAll();
    }

    @Test
    public void givenAnInvalidNullName_whenCallsSave_ShouldReturError() {
        final var expectedPropertyName = "name";
        final var expectedMessage = "not-null property references a null or transient value : com.ioliveira.catalogo.infrastructure.category.persistence.CategoryJpaEntity.name";

        final var category = Category.newCategory("Filmes", "A categoria mais assistida", true);

        final var entity = CategoryJpaEntity.from(category);
        entity.setName(null);

        final var exception =
                assertThrows(DataIntegrityViolationException.class, () -> categoryRepository.save(entity));

        final var cause = assertInstanceOf(PropertyValueException.class, exception.getCause());

        assertEquals(expectedPropertyName, cause.getPropertyName());
        assertEquals(expectedMessage, cause.getMessage());
    }

    @Test
    public void givenAnInvalidNullCreatedAt_whenCallsSave_ShouldReturError() {
        final var expectedPropertyName = "createdAt";
        final var expectedMessage = "not-null property references a null or transient value : com.ioliveira.catalogo.infrastructure.category.persistence.CategoryJpaEntity.createdAt";

        final var category = Category.newCategory("Filmes", "A categoria mais assistida", true);

        final var entity = CategoryJpaEntity.from(category);
        entity.setCreatedAt(null);

        final var exception =
                assertThrows(DataIntegrityViolationException.class, () -> categoryRepository.save(entity));

        final var cause = assertInstanceOf(PropertyValueException.class, exception.getCause());

        assertEquals(expectedPropertyName, cause.getPropertyName());
        assertEquals(expectedMessage, cause.getMessage());
    }

    @Test
    public void givenAnInvalidNullUpdatedAt_whenCallsSave_ShouldReturError() {
        final var expectedPropertyName = "updatedAt";
        final var expectedMessage = "not-null property references a null or transient value : com.ioliveira.catalogo.infrastructure.category.persistence.CategoryJpaEntity.updatedAt";

        final var category = Category.newCategory("Filmes", "A categoria mais assistida", true);

        final var entity = CategoryJpaEntity.from(category);
        entity.setUpdatedAt(null);

        final var exception =
                assertThrows(DataIntegrityViolationException.class, () -> categoryRepository.save(entity));

        final var cause = assertInstanceOf(PropertyValueException.class, exception.getCause());

        assertEquals(expectedPropertyName, cause.getPropertyName());
        assertEquals(expectedMessage, cause.getMessage());
    }
}
