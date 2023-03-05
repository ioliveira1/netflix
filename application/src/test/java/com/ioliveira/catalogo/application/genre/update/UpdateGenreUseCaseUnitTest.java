package com.ioliveira.catalogo.application.genre.update;

import com.ioliveira.catalogo.domain.category.CategoryGateway;
import com.ioliveira.catalogo.domain.category.CategoryID;
import com.ioliveira.catalogo.domain.exceptions.NotificationException;
import com.ioliveira.catalogo.domain.genre.Genre;
import com.ioliveira.catalogo.domain.genre.GenreGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UpdateGenreUseCaseUnitTest {

    @InjectMocks
    private DefaultUpdateGenreUseCase useCase;

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
    public void givenAValidCommand_whenCallsUpdateGenre_shouldReturnGenreId() {
        final Genre genre = Genre.newGenre("accao", true);

        final var expectegId = genre.getId();
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.<CategoryID>of();

        final var command =
                UpdateGenreCommand.with(expectegId.getValue(), expectedName, expectedIsActive, asString(expectedCategories));

        when(genreGateway.findById(any())).thenReturn(Optional.of(Genre.with(genre)));

        when(genreGateway.update(any())).thenAnswer(returnsFirstArg());

        final var output = useCase.execute(command);

        assertNotNull(output);
        assertEquals(expectegId.getValue(), output.id());

        verify(genreGateway, times(1)).findById(eq(expectegId));

        verify(genreGateway, times(1)).update(argThat(updatedGenre ->
                Objects.equals(expectegId, updatedGenre.getId())
                        && Objects.equals(expectedName, updatedGenre.getName())
                        && Objects.equals(expectedIsActive, updatedGenre.isActive())
                        && Objects.equals(expectedCategories, updatedGenre.getCategories())
                        && Objects.equals(genre.getCreatedAt(), updatedGenre.getCreatedAt())
                        && genre.getUpdatedAt().isBefore(updatedGenre.getUpdatedAt())
                        && Objects.isNull(updatedGenre.getDeletedAt())
        ));
    }

    @Test
    public void givenAValidCommandWithCategories_whenCallsUpdateGenre_shouldReturnGenreId() {
        final Genre genre = Genre.newGenre("accao", true);

        final var expectegId = genre.getId();
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.of(CategoryID.from("123"), CategoryID.from("456"));

        final var command =
                UpdateGenreCommand.with(expectegId.getValue(), expectedName, expectedIsActive, asString(expectedCategories));

        when(genreGateway.findById(any())).thenReturn(Optional.of(Genre.with(genre)));

        when(categoryGateway.existsByIds(any())).thenReturn(expectedCategories);

        when(genreGateway.update(any())).thenAnswer(returnsFirstArg());

        final var output = useCase.execute(command);

        assertNotNull(output);
        assertEquals(expectegId.getValue(), output.id());

        verify(genreGateway, times(1)).findById(eq(expectegId));

        verify(categoryGateway, times(1)).existsByIds((eq(expectedCategories)));

        verify(genreGateway, times(1)).update(argThat(updatedGenre ->
                Objects.equals(expectegId, updatedGenre.getId())
                        && Objects.equals(expectedName, updatedGenre.getName())
                        && Objects.equals(expectedIsActive, updatedGenre.isActive())
                        && Objects.equals(expectedCategories, updatedGenre.getCategories())
                        && Objects.equals(genre.getCreatedAt(), updatedGenre.getCreatedAt())
                        && genre.getUpdatedAt().isBefore(updatedGenre.getUpdatedAt())
                        && Objects.isNull(updatedGenre.getDeletedAt())
        ));
    }

    @Test
    public void givenAnInvalidName_whenCallsUpdateGenre_shouldReturnError() {
        final Genre genre = Genre.newGenre("accao", true);

        final var expectegId = genre.getId();
        final var expectedIsActive = true;
        final var expectedCategories = List.of(CategoryID.from("123"), CategoryID.from("456"));
        final var expectedErrorMessage = "'name' should not be null";
        final var expectedErrorCount = 1;

        final var command =
                UpdateGenreCommand.with(expectegId.getValue(), null, expectedIsActive, asString(expectedCategories));

        when(genreGateway.findById(any())).thenReturn(Optional.of(Genre.with(genre)));

        when(categoryGateway.existsByIds(any())).thenReturn(expectedCategories);

        final NotificationException exception = assertThrows(NotificationException.class, () -> useCase.execute(command));

        assertNotNull(exception);
        assertEquals(expectedErrorCount, exception.getErrors().size());
        assertEquals(expectedErrorMessage, exception.getErrors().get(0).message());
    }

    @Test
    public void givenAnInvalidNameAndInvalidCategories_whenCallsUpdateGenre_shouldReturnError() {
        final Genre genre = Genre.newGenre("accao", true);

        final var expectegId = genre.getId();
        final var expectedIsActive = true;
        final var expectedCategories = List.of(CategoryID.from("123"), CategoryID.from("456"));
        final var expectedErrorMessageOne = "Some categories could not be found: 456";
        final var expectedErrorMessageTwo = "'name' should not be null";
        final var expectedErrorCount = 2;

        final var command =
                UpdateGenreCommand.with(expectegId.getValue(), null, expectedIsActive, asString(expectedCategories));

        when(genreGateway.findById(any())).thenReturn(Optional.of(Genre.with(genre)));

        when(categoryGateway.existsByIds(any()))
                .thenReturn(List.of(CategoryID.from("123")));

        final NotificationException exception = assertThrows(NotificationException.class, () -> useCase.execute(command));

        assertNotNull(exception);
        assertEquals(expectedErrorCount, exception.getErrors().size());
        assertEquals(expectedErrorMessageOne, exception.getErrors().get(0).message());
        assertEquals(expectedErrorMessageTwo, exception.getErrors().get(1).message());
    }

    @Test
    public void givenAnInvalidCommandWithCategories_whenCallsUpdateGenre_shouldReturnError() {
        final Genre genre = Genre.newGenre("accao", true);

        final var expectegId = genre.getId();
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.of(CategoryID.from("123"), CategoryID.from("456"));
        final var expectedErrorMessage = "Some categories could not be found: 456";
        final var expectedErrorCount = 1;

        final var command =
                UpdateGenreCommand.with(expectegId.getValue(), expectedName, expectedIsActive, asString(expectedCategories));

        when(genreGateway.findById(any())).thenReturn(Optional.of(Genre.with(genre)));

        when(categoryGateway.existsByIds(any()))
                .thenReturn(List.of(CategoryID.from("123")));

        final NotificationException exception = assertThrows(NotificationException.class, () -> useCase.execute(command));

        assertNotNull(exception);
        assertEquals(expectedErrorCount, exception.getErrors().size());
        assertEquals(expectedErrorMessage, exception.getErrors().get(0).message());
    }

    private List<String> asString(final List<CategoryID> list) {
        return list.stream()
                .map(CategoryID::getValue)
                .toList();
    }

}
