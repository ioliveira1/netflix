package com.ioliveira.catalogo.application.genre.delete;

import com.ioliveira.catalogo.domain.genre.Genre;
import com.ioliveira.catalogo.domain.genre.GenreGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class DeleteGenreUseCaseUnitTest {

    @InjectMocks
    private DefaultDeleteGenreUseCase useCase;

    @Mock
    private GenreGateway genreGateway;

    @BeforeEach
    void cleanUp() {
        reset(genreGateway);
    }

    @Test
    public void givenAValidCommand_whenCallsDeleteGenre_shouldDeleteGenre() {
        final Genre genre = Genre.newGenre("accao", true);

        final var expectegId = genre.getId();

        doNothing().when(genreGateway).deleteById(any());

        assertDoesNotThrow(() -> useCase.execute(expectegId.getValue()));

        verify(genreGateway, times(1)).deleteById(expectegId);
    }

}
