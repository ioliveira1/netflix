package com.ioliveira.catalogo.application.category.retrieve.get;

import com.ioliveira.catalogo.domain.category.Category;
import com.ioliveira.catalogo.domain.category.CategoryGateway;
import com.ioliveira.catalogo.domain.category.CategoryID;
import com.ioliveira.catalogo.domain.exceptions.DomainException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GetCategoryByIdUsecaseUnitTest {

    @InjectMocks
    private DefaultGetCategoryByIdUsecase useCase;

    @Mock
    private CategoryGateway categoryGateway;

    @BeforeEach
    void cleanUp() {
        reset(categoryGateway);
    }

    @Test
    public void givenAValidId_whenCallsGetCategory_thenShouldReturnCategory() {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var category = Category.newCategory(expectedName, expectedDescription, expectedIsActive);
        final var expectedId = category.getId();

        when(categoryGateway.findById(expectedId)).thenReturn(Optional.of(category.clone()));

        final var output = useCase.execute(expectedId.getValue());

        assertNotNull(output);
        assertNotNull(output.id());
        assertEquals(expectedName, output.name());
    }

    @Test
    public void givenAnInvalidId_whenCallsGetCategory_thenShouldReturnNotFound() {
        final var expectedErrorMessage = "Category with ID 123 not found";
        final var expectedId = CategoryID.from("123");

        when(categoryGateway.findById(expectedId)).thenReturn(Optional.empty());

        final var exception = assertThrows(DomainException.class, () -> useCase.execute(expectedId.getValue()));

        assertEquals(expectedErrorMessage, exception.getErrors().get(0).message());
    }

    @Test
    public void givenAValidId_whenGatewayThrosException_thenShouldReturnException() {
        final var expectedErrorMessage = "Gateway error";
        final var expectedId = CategoryID.from("123");

        when(categoryGateway.findById(expectedId)).thenThrow(new RuntimeException(expectedErrorMessage));

        final var exception = assertThrows(RuntimeException.class, () -> useCase.execute(expectedId.getValue()));

        assertEquals(expectedErrorMessage, exception.getMessage());
    }
}
