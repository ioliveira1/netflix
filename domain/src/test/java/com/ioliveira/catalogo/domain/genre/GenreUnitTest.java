package com.ioliveira.catalogo.domain.genre;

import com.ioliveira.catalogo.domain.category.CategoryID;
import com.ioliveira.catalogo.domain.exceptions.NotificationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GenreUnitTest {

    @Test
    public void givenValidParams_whenCallsNewGenre_thenInstantiateANewGenre() {
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = 0;

        final var genre = Genre.newGenre(expectedName, expectedIsActive);

        assertNotNull(genre);
        assertNotNull(genre.getId());
        assertEquals(expectedName, genre.getName());
        assertEquals(expectedIsActive, genre.isActive());
        assertEquals(expectedCategories, genre.getCategories().size());
        assertNotNull(genre.getCreatedAt());
        assertNotNull(genre.getUpdatedAt());
        assertNull(genre.getDeletedAt());
    }

    @Test
    public void givenInvalidNullName_whenCallsNewGenre_thenReciveAnError() {
        final var expectedIsActive = true;
        final var expectedErrorMessage = "'name' should not be null";
        final var expectedErrorCount = 1;

        final var exception =
                assertThrows(NotificationException.class, () -> Genre.newGenre(null, expectedIsActive));

        assertEquals(expectedErrorMessage, exception.getErrors().get(0).message());
        assertEquals(expectedErrorCount, exception.getErrors().size());
    }

    @Test
    public void givenInvalidEmptyName_whenCallsNewGenre_thenReciveAnError() {
        final String expectedName = " ";
        final var expectedIsActive = true;
        final var expectedErrorMessage = "'name' should not be empty";
        final var expectedErrorCount = 1;

        final var exception =
                assertThrows(NotificationException.class, () -> Genre.newGenre(expectedName, expectedIsActive));

        assertEquals(expectedErrorMessage, exception.getErrors().get(0).message());
        assertEquals(expectedErrorCount, exception.getErrors().size());
    }

    @Test
    public void givenAnInvalidNameLengthMoreThan255_whenCallsNewGenre_thenReciveAnError() {
        final var expectedName = """
                Lorem ipsum dolor sit amet. Aut sapiente nulla sit minus vitae eos voluptas tempore. Quo perferendis quibusdam
                et blanditiis dolor aut esse accusamus sed doloribus rerum. Et iusto repudiandae est necessitatibus inventore
                sit aliquid odio est laborum consectetur.
                """;
        final var expectedIsActive = true;
        final var expectedErrorMessage = "'name' must be between 3 and 255 characters";
        final var expectedErrorCount = 1;

        final var exception =
                assertThrows(NotificationException.class, () -> Genre.newGenre(expectedName, expectedIsActive));

        assertEquals(expectedErrorMessage, exception.getErrors().get(0).message());
        assertEquals(expectedErrorCount, exception.getErrors().size());
    }

    @Test
    public void givenAnInvalidNameLengthLessThan3_whenCallsNewGenre_thenReciveAnError() {
        final var expectedName = "Aç ";
        final var expectedIsActive = true;
        final var expectedErrorMessage = "'name' must be between 3 and 255 characters";
        final var expectedErrorCount = 1;

        final var exception =
                assertThrows(NotificationException.class, () -> Genre.newGenre(expectedName, expectedIsActive));

        assertEquals(expectedErrorMessage, exception.getErrors().get(0).message());
        assertEquals(expectedErrorCount, exception.getErrors().size());
    }

    @Test
    public void givenAValidActiveGenre_whenCallsDeactivate_thenReturnGenreInactivated() {
        final var expectedName = "Drama";
        final var expectedIsActive = false;
        final var expectedCategories = 0;

        final var genre = Genre.newGenre(expectedName, true);

        assertNotNull(genre);
        assertTrue(genre.isActive());
        assertNull(genre.getDeletedAt());

        genre.deactivate();

        assertNotNull(genre.getId());
        assertEquals(expectedName, genre.getName());
        assertEquals(expectedIsActive, genre.isActive());
        assertEquals(expectedCategories, genre.getCategories().size());
        assertNotNull(genre.getCreatedAt());
        assertTrue(genre.getUpdatedAt().isAfter(genre.getCreatedAt()));
        assertNotNull(genre.getDeletedAt());
    }

    @Test
    public void givenAValidInactiveGenre_whenCallsDeactivate_thenReturnGenreActivated() {
        final var expectedName = "Drama";
        final var expectedIsActive = true;
        final var expectedCategories = 0;

        final var genre = Genre.newGenre(expectedName, false);

        assertNotNull(genre);
        assertFalse(genre.isActive());
        assertNotNull(genre.getDeletedAt());

        final var createdAt = genre.getCreatedAt();
        final var updatedAt = genre.getUpdatedAt();

        genre.activate();

        assertNotNull(genre.getId());
        assertEquals(expectedName, genre.getName());
        assertEquals(expectedIsActive, genre.isActive());
        assertEquals(createdAt, genre.getCreatedAt());
        assertEquals(expectedCategories, genre.getCategories().size());
        assertTrue(updatedAt.isBefore(genre.getUpdatedAt()));
        assertTrue(genre.getUpdatedAt().isAfter(genre.getCreatedAt()));
        assertNull(genre.getDeletedAt());
    }

    @Test
    public void givenAValidGenre_whenCallsUpdate_thenReturnGenreUpdated() {
        final var expectedName = "Drama";
        final var expectedIsActive = true;
        final var expectedCategories = List.of(CategoryID.from("123"));

        final var genre = Genre.newGenre("Dramaaaa", false);

        final var createdAt = genre.getCreatedAt();
        final var updatedAt = genre.getUpdatedAt();

        final var genreUpdated = genre.update(expectedName, expectedIsActive, expectedCategories);

        assertNotNull(genreUpdated.getId());
        assertEquals(expectedName, genreUpdated.getName());
        assertEquals(expectedIsActive, genreUpdated.isActive());
        assertEquals(expectedCategories, genreUpdated.getCategories());
        assertEquals(createdAt, genreUpdated.getCreatedAt());
        assertTrue(updatedAt.isBefore(genreUpdated.getUpdatedAt()));
        assertNull(genreUpdated.getDeletedAt());
    }

    @Test
    public void givenAValidGenre_whenCallsUpdateWithEmptyName_thenReturnError() {
        final var expectedName = " ";
        final var expectedIsActive = true;
        final var expectedCategories = List.of(CategoryID.from("123"));
        final var expectedErrorMessage = "'name' should not be empty";
        final var expectedErrorCount = 1;

        final var genre = Genre.newGenre("dRama", false);

        final var exception =
                assertThrows(NotificationException.class,
                        () -> genre.update(expectedName, expectedIsActive, expectedCategories));

        assertEquals(expectedErrorMessage, exception.getErrors().get(0).message());
        assertEquals(expectedErrorCount, exception.getErrors().size());
    }

    @Test
    public void givenAValidGenre_whenCallsUpdateWithNullCategories_thenShouldReturnOk() {
        final var expectedName = "Ação";
        final var expectedIsActive = true;

        final var genre = Genre.newGenre(expectedName, expectedIsActive);

        Assertions.assertDoesNotThrow(() -> genre.update(expectedName, expectedIsActive, Collections.emptyList()));

        assertNotNull(genre);
        assertNotNull(genre.getId());
        assertEquals(expectedName, genre.getName());
        assertEquals(expectedIsActive, genre.isActive());
        assertTrue(genre.getCategories().isEmpty());
        assertNotNull(genre.getCreatedAt());
        assertNotNull(genre.getUpdatedAt());
        assertNull(genre.getDeletedAt());
    }

    @Test
    public void givenAValidGenre_whenCallsUpdateWithEmptyCategories_thenShouldReturnOk() {
        final var expectedName = "Ação";
        final var expectedIsActive = true;

        final var genre = Genre.newGenre(expectedName, expectedIsActive);

        Assertions.assertDoesNotThrow(() -> genre.update(expectedName, expectedIsActive, Collections.emptyList()));

        assertNotNull(genre);
        assertNotNull(genre.getId());
        assertEquals(expectedName, genre.getName());
        assertEquals(expectedIsActive, genre.isActive());
        assertTrue(genre.getCategories().isEmpty());
        assertNotNull(genre.getCreatedAt());
        assertNotNull(genre.getUpdatedAt());
        assertNull(genre.getDeletedAt());
    }

    @Test
    public void givenAValidEmptyCategoriesGenre_whenCallsAddCategory_thenShouldReturnOk() {
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var seriesID = CategoryID.from("132");
        final var moviesID = CategoryID.from("456");
        final var expectedCategories = List.of(seriesID, moviesID);

        final var genre = Genre.newGenre(expectedName, expectedIsActive);

        assertEquals(0, genre.getCategories().size());

        final var createdAt = genre.getCreatedAt();
        final var updatedAt = genre.getUpdatedAt();

        genre.addCategory(seriesID);
        genre.addCategory(moviesID);

        assertNotNull(genre);
        assertNotNull(genre.getId());
        assertEquals(expectedName, genre.getName());
        assertEquals(expectedIsActive, genre.isActive());
        assertEquals(2, genre.getCategories().size());
        assertEquals(expectedCategories, genre.getCategories());
        assertEquals(createdAt, genre.getCreatedAt());
        assertTrue(updatedAt.isBefore(genre.getUpdatedAt()));
        assertNull(genre.getDeletedAt());
    }

    @Test
    public void givenAValidGenre_whenCallsDeleteCategory_thenShouldReturnOk() {
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var seriesID = CategoryID.from("132");
        final var moviesID = CategoryID.from("456");
        final var expectedCategories = List.of(moviesID);

        final var genre = Genre.newGenre(expectedName, expectedIsActive);
        genre.update(expectedName, expectedIsActive, List.of(seriesID, moviesID));

        assertEquals(2, genre.getCategories().size());

        final var createdAt = genre.getCreatedAt();
        final var updatedAt = genre.getUpdatedAt();

        genre.removeCategory(seriesID);

        assertNotNull(genre);
        assertNotNull(genre.getId());
        assertEquals(expectedName, genre.getName());
        assertEquals(expectedIsActive, genre.isActive());
        assertEquals(1, genre.getCategories().size());
        assertEquals(expectedCategories, genre.getCategories());
        assertEquals(createdAt, genre.getCreatedAt());
        assertTrue(updatedAt.isBefore(genre.getUpdatedAt()));
        assertNull(genre.getDeletedAt());
    }
}
