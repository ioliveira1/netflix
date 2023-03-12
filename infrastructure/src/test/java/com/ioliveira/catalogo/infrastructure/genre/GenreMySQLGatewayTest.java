package com.ioliveira.catalogo.infrastructure.genre;

import com.ioliveira.catalogo.MySQLGatewayTest;
import com.ioliveira.catalogo.domain.category.Category;
import com.ioliveira.catalogo.domain.category.CategoryID;
import com.ioliveira.catalogo.domain.genre.Genre;
import com.ioliveira.catalogo.domain.genre.GenreID;
import com.ioliveira.catalogo.domain.pagination.SearchQuery;
import com.ioliveira.catalogo.infrastructure.category.CategoryMySQLGateway;
import com.ioliveira.catalogo.infrastructure.genre.persistence.GenreJpaEntity;
import com.ioliveira.catalogo.infrastructure.genre.persistence.GenreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@MySQLGatewayTest
public class GenreMySQLGatewayTest {

    @Autowired
    private CategoryMySQLGateway categoryMySQLGateway;

    @Autowired
    private GenreMySQLGateway genreMySQLGateway;

    @Autowired
    private GenreRepository genreRepository;

    @BeforeEach
    public void cleanUp() {
        this.genreRepository.deleteAll();
    }

    @Test
    public void givenAValidGenre_WhenCallsCreateGenre_ShouldReturnANewGenre() {
        final var filmes =
                categoryMySQLGateway.create(Category.newCategory("Filmes", null, true));

        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.of(filmes.getId());

        final Genre genre = Genre.newGenre(expectedName, expectedIsActive);
        genre.addCategories(expectedCategories);

        assertEquals(0, genreRepository.count());

        final var actualGenre = genreMySQLGateway.create(genre);

        assertEquals(1, genreRepository.count());

        assertEquals(genre.getId(), actualGenre.getId());
        assertEquals(expectedName, actualGenre.getName());
        assertEquals(expectedIsActive, actualGenre.isActive());
        assertEquals(expectedCategories, actualGenre.getCategories());
        assertEquals(genre.getCreatedAt(), actualGenre.getCreatedAt());
        assertEquals(genre.getUpdatedAt(), actualGenre.getUpdatedAt());
        assertEquals(genre.getDeletedAt(), actualGenre.getDeletedAt());
        assertNull(actualGenre.getDeletedAt());

        final var persistedGenre = genreRepository.findById(genre.getId().getValue()).get();

        assertEquals(expectedName, persistedGenre.getName());
        assertEquals(expectedIsActive, persistedGenre.isActive());
        assertEquals(expectedCategories, persistedGenre.getCategoryIDs());
        assertEquals(genre.getCreatedAt(), persistedGenre.getCreatedAt());
        assertEquals(genre.getUpdatedAt(), persistedGenre.getUpdatedAt());
        assertEquals(genre.getDeletedAt(), persistedGenre.getDeletedAt());
        assertNull(persistedGenre.getDeletedAt());
    }

    @Test
    public void givenAValidGenreWithoutCategories_WhenCallsCreateGenre_ShouldReturnANewGenre() {
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.<CategoryID>of();

        final Genre genre = Genre.newGenre(expectedName, expectedIsActive);

        assertEquals(0, genreRepository.count());

        final var actualGenre = genreMySQLGateway.create(genre);

        assertEquals(1, genreRepository.count());

        assertEquals(genre.getId(), actualGenre.getId());
        assertEquals(expectedName, actualGenre.getName());
        assertEquals(expectedIsActive, actualGenre.isActive());
        assertEquals(expectedCategories, actualGenre.getCategories());
        assertEquals(genre.getCreatedAt(), actualGenre.getCreatedAt());
        assertEquals(genre.getUpdatedAt(), actualGenre.getUpdatedAt());
        assertEquals(genre.getDeletedAt(), actualGenre.getDeletedAt());
        assertNull(actualGenre.getDeletedAt());

        final var persistedGenre = genreRepository.findById(genre.getId().getValue()).get();

        assertEquals(expectedName, persistedGenre.getName());
        assertEquals(expectedIsActive, persistedGenre.isActive());
        assertEquals(expectedCategories, persistedGenre.getCategoryIDs());
        assertEquals(genre.getCreatedAt(), persistedGenre.getCreatedAt());
        assertEquals(genre.getUpdatedAt(), persistedGenre.getUpdatedAt());
        assertEquals(genre.getDeletedAt(), persistedGenre.getDeletedAt());
        assertNull(persistedGenre.getDeletedAt());
    }

    @Test
    public void givenAValidGenreWithoutCategories_WhenCallsUpdateGenre_ShouldReturnANewGenre() {
        final var filmes =
                categoryMySQLGateway.create(Category.newCategory("Filmes", null, true));

        final var series =
                categoryMySQLGateway.create(Category.newCategory("Séries", null, true));

        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.of(filmes.getId(), series.getId());

        final Genre genre = Genre.newGenre("acc", expectedIsActive);

        assertEquals(0, genreRepository.count());

        genreRepository.saveAndFlush(GenreJpaEntity.from(genre));

        assertEquals("acc", genre.getName());
        assertEquals(0, genre.getCategories().size());


        final var actualGenre = genreMySQLGateway.update(
                Genre
                        .with(genre)
                        .update(expectedName, expectedIsActive, expectedCategories)
        );

        assertEquals(1, genreRepository.count());

        assertEquals(genre.getId(), actualGenre.getId());
        assertEquals(expectedName, actualGenre.getName());
        assertEquals(expectedIsActive, actualGenre.isActive());
        assertTrue(expectedCategories.containsAll(actualGenre.getCategories()));
        assertEquals(genre.getCreatedAt(), actualGenre.getCreatedAt());
        assertTrue(genre.getUpdatedAt().isBefore(actualGenre.getUpdatedAt()));
        assertEquals(genre.getDeletedAt(), actualGenre.getDeletedAt());

        final var persistedGenre = genreRepository.findById(genre.getId().getValue()).get();

        assertEquals(expectedName, persistedGenre.getName());
        assertEquals(expectedIsActive, persistedGenre.isActive());
        assertTrue(expectedCategories.containsAll(persistedGenre.getCategoryIDs()));
        assertEquals(genre.getCreatedAt(), persistedGenre.getCreatedAt());
        assertTrue(genre.getUpdatedAt().isBefore(persistedGenre.getUpdatedAt()));
        assertEquals(genre.getDeletedAt(), persistedGenre.getDeletedAt());
    }

    @Test
    public void givenAValidPersistedGenre_WhenCallsDeleteGenre_ShouldDeleteGenre() {
        final Genre genre = Genre.newGenre("Ação", true);

        assertEquals(0, genreRepository.count());

        genreRepository.saveAndFlush(GenreJpaEntity.from(genre));

        assertEquals(1, genreRepository.count());

        genreMySQLGateway.deleteById(genre.getId());

        assertEquals(0, genreRepository.count());
    }

    @Test
    public void givenAnInvalidId_WhenCallsDeleteGenre_ShouldReturnOk() {
        final Genre genre = Genre.newGenre("Ação", true);

        assertEquals(0, genreRepository.count());

        genreRepository.saveAndFlush(GenreJpaEntity.from(genre));

        assertEquals(1, genreRepository.count());

        genreMySQLGateway.deleteById(GenreID.from("invalid"));

        assertEquals(1, genreRepository.count());
    }

    @Test
    public void givenAPersistedGenre_WhenCallsFindById_ShouldReturnAGenre() {
        final var expectedName = "Ação";
        final var expectedIsActive = true;

        final var genre = Genre.newGenre(expectedName, expectedIsActive);
        final var expectedId = genre.getId();

        assertEquals(0, genreRepository.count());

        genreRepository.saveAndFlush(GenreJpaEntity.from(genre));

        assertEquals(1, genreRepository.count());

        final var genreFound = genreMySQLGateway.findById(expectedId).get();

        assertEquals(expectedId, genreFound.getId());
        assertEquals(expectedName, genreFound.getName());
        assertEquals(expectedIsActive, genreFound.isActive());
        assertEquals(genre.getCreatedAt(), genreFound.getCreatedAt());
        assertEquals(genre.getUpdatedAt(), genreFound.getUpdatedAt());
        assertNull(genreFound.getDeletedAt());
    }

    @Test
    public void givenAnInvalidId_WhenCallsFindById_ShouldReturnEmpty() {
        assertEquals(0, genreRepository.count());

        final var genre = genreMySQLGateway.findById(GenreID.from("invalid"));

        assertTrue(genre.isEmpty());
    }

    @Test
    public void givenEmptyGenres_WhenCallsFindAll_ShouldReturnEmptyList() {
        final var expectedPage = 0;
        final var expectedPerPage = 1;
        final var expectedTerms = "";
        final var expectedSort = "name";
        final var expectedDirection = "asc";

        final var query =
                new SearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        assertEquals(0, genreRepository.count());

        final var result = genreMySQLGateway.findAll(query);

        assertEquals(expectedPage, result.currentPage());
        assertEquals(expectedPerPage, result.perPage());
        assertEquals(0, result.total());
        assertEquals(0, result.items().size());
    }

    @ParameterizedTest
    @CsvSource({
            "Aç,0,10,1,1,Ação",
            "dr,0,10,1,1,Drama",
            "com,0,10,1,1,Comédia"
    })
    public void givenAValidTerm_WhenCallsFindAll_ShouldReturnFiltered(
            final String expectedTerms,
            final int expectedPage,
            final int expectedPerPage,
            final int expectedItemsCount,
            final long expectedTotal,
            final String expectedGenreName
    ) {
        final var expectedSort = "name";
        final var expectedDirection = "asc";

        assertEquals(0, genreRepository.count());

        mockGenres();

        assertEquals(3, genreRepository.count());

        final var query =
                new SearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);


        final var result = genreMySQLGateway.findAll(query);

        assertEquals(expectedPage, result.currentPage());
        assertEquals(expectedPerPage, result.perPage());
        assertEquals(expectedTotal, result.total());
        assertEquals(expectedItemsCount, result.items().size());
        assertEquals(expectedGenreName, result.items().get(0).getName());
    }

    private void mockGenres() {
        genreRepository.saveAllAndFlush(List.of(
                GenreJpaEntity.from(Genre.newGenre("Comédia", true)),
                GenreJpaEntity.from(Genre.newGenre("Ação", true)),
                GenreJpaEntity.from(Genre.newGenre("Drama", true))
        ));

    }
}
