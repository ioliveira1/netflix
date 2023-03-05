package com.ioliveira.catalogo.application.genre.retrieve.get;

import com.ioliveira.catalogo.domain.exceptions.NotFoundException;
import com.ioliveira.catalogo.domain.genre.Genre;
import com.ioliveira.catalogo.domain.genre.GenreGateway;
import com.ioliveira.catalogo.domain.genre.GenreID;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GetGenreByIdUseCaseUnitTest {

    @InjectMocks
    private DefaultGetGenreByIdUseCase useCase;

    @Mock
    private GenreGateway genreGateway;

    @BeforeEach
    void cleanUp() {
        reset(genreGateway);
    }

    @Test
    public void givenAValidId_whenCallsGetGenreById_shouldReturnGenre() {
        final var genre = Genre.newGenre("Ação", true);
        final var expectedId = genre.getId();

        when(genreGateway.findById(any())).thenReturn(Optional.of(genre));

        final var output = useCase.execute(expectedId.getValue());

        assertNotNull(output);
        assertEquals(expectedId.getValue(), output.id());

        verify(genreGateway, times(1)).findById(eq(expectedId));
    }

    @Test
    public void givenAValidId_whenCallsGetGenreByIdAndNotExists_shouldReturnNotFound() {
        final var expectedId = GenreID.from("123");
        final var expectedErrorMessage = "Genre ID 123 was not found";

        when(genreGateway.findById(eq(expectedId))).thenReturn(Optional.empty());

        final var exception =
                assertThrows(NotFoundException.class, () -> useCase.execute(expectedId.getValue()));

        assertEquals(expectedErrorMessage, exception.getErrors().get(0).message());

        verify(genreGateway, times(1)).findById(eq(expectedId));
    }

}
