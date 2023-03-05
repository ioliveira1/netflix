package com.ioliveira.catalogo.application.genre.create;

import com.ioliveira.catalogo.domain.category.CategoryGateway;
import com.ioliveira.catalogo.domain.category.CategoryID;
import com.ioliveira.catalogo.domain.exceptions.NotificationException;
import com.ioliveira.catalogo.domain.genre.GenreGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CreateGenreUseCaseUnitTest {

    @InjectMocks
    private DefaultCreateGenreUseCase useCase;

    @Mock
    private GenreGateway genreGateway;

    @Mock
    private CategoryGateway categoryGateway;

    @BeforeEach
    void cleanUp() {
        reset(genreGateway);
        reset(categoryGateway);
    }

    @Test
    public void givenAValidCommand_whenCallsCreateGenre_shouldReturnGenreId() {
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.<CategoryID>of();

        final var command = CreateGenreCommand.with(expectedName, expectedIsActive, asString(expectedCategories));

        when(genreGateway.create(any())).thenAnswer(returnsFirstArg());

        final var output = useCase.execute(command);

        assertNotNull(output);
        assertNotNull(output.id());

        verify(genreGateway, times(1))
                .create(argThat(genre ->
                        Objects.equals(expectedName, genre.getName())
                                && Objects.equals(expectedIsActive, genre.isActive())
                                && Objects.equals(expectedCategories, genre.getCategories())
                                && Objects.nonNull(genre.getId())
                                && Objects.nonNull(genre.getCreatedAt())
                                && Objects.nonNull(genre.getUpdatedAt())
                                && Objects.isNull(genre.getDeletedAt())
                ));
    }

    @Test
    public void givenAValidCommandWithCategories_whenCallsCreateGenre_shouldReturnGenreId() {
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.of(
                CategoryID.from("123"),
                CategoryID.from("456")
        );

        final var command =
                CreateGenreCommand.with(expectedName, expectedIsActive, asString(expectedCategories));

        when(categoryGateway.existsByIds(any())).thenReturn(expectedCategories);

        when(genreGateway.create(any())).thenAnswer(returnsFirstArg());

        final var output = useCase.execute(command);

        assertNotNull(output);
        assertNotNull(output.id());

        verify(categoryGateway, times(1)).existsByIds(expectedCategories);

        verify(genreGateway, times(1))
                .create(argThat(genre ->
                        Objects.equals(expectedName, genre.getName())
                                && Objects.equals(expectedIsActive, genre.isActive())
                                && Objects.equals(expectedCategories, genre.getCategories())
                                && Objects.nonNull(genre.getId())
                                && Objects.nonNull(genre.getCreatedAt())
                                && Objects.nonNull(genre.getUpdatedAt())
                                && Objects.isNull(genre.getDeletedAt())
                ));
    }

    @Test
    public void givenAnInvalidEmptyName_whenCallsCreateGenre_shouldReturnDomainException() {
        final var expectedName = " ";
        final var expectedIsActive = true;
        final var expectedCategories = List.<CategoryID>of();
        final var expectedErrorMessage = "'name' should not be empty";
        final var expectedErrorCount = 1;


        final var command =
                CreateGenreCommand.with(expectedName, expectedIsActive, asString(expectedCategories));

        final var exception =
                assertThrows(NotificationException.class, () -> useCase.execute(command));

        assertNotNull(exception);
        assertEquals(expectedErrorMessage, exception.getErrors().get(0).message());
        assertEquals(expectedErrorCount, exception.getErrors().size());

        verify(categoryGateway, times(0)).existsByIds(any());
        verify(genreGateway, times(0)).create(any());
    }

    @Test
    public void givenAValidCommandWithSomeInvalidCategories_whenCallsCreateGenre_shouldReturnDomainException() {
        final var expectedName = " ";
        final var expectedIsActive = true;
        final var expectedCategories = List.of(
                CategoryID.from("123"),
                CategoryID.from("456"),
                CategoryID.from("789")
        );
        final var expectedErrorMessage1 = "Some categories could not be found: 789";
        final var expectedErrorMessage2 = "'name' should not be empty";
        final var expectedErrorCount = 2;

        final var command =
                CreateGenreCommand.with(expectedName, expectedIsActive, asString(expectedCategories));

        when(categoryGateway.existsByIds(any()))
                .thenReturn(List.of(CategoryID.from("123"), CategoryID.from("456")));

        final var exception =
                assertThrows(NotificationException.class, () -> useCase.execute(command));

        assertNotNull(exception);
        assertEquals(expectedErrorMessage1, exception.getErrors().get(0).message());
        assertEquals(expectedErrorMessage2, exception.getErrors().get(1).message());
        assertEquals(expectedErrorCount, exception.getErrors().size());
    }

    private List<String> asString(final List<CategoryID> list) {
        return list.stream()
                .map(CategoryID::getValue)
                .toList();
    }

}
