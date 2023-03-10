package com.ioliveira.catalogo.integration.category.retrieve.list;

import com.ioliveira.catalogo.IntegrationTest;
import com.ioliveira.catalogo.application.category.retrieve.list.ListCategoriesUseCase;
import com.ioliveira.catalogo.domain.category.Category;
import com.ioliveira.catalogo.domain.pagination.SearchQuery;
import com.ioliveira.catalogo.infrastructure.category.persistence.CategoryJpaEntity;
import com.ioliveira.catalogo.infrastructure.category.persistence.CategoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.stream.Stream;

@IntegrationTest
public class ListCategoriesUseCaseIT {

    @Autowired
    private ListCategoriesUseCase useCase;

    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeEach
    public void mockUp() {
        categoryRepository.deleteAll();

        final var categories = Stream.of(
                Category.newCategory("Filmes", null, true),
                Category.newCategory("Netflix Originals", "Títulos de autoria da Netflix", true),
                Category.newCategory("Amazon Originals", "Títulos de autoria da Amazon", true),
                Category.newCategory("Documentários", null, true),
                Category.newCategory("Sports", null, true),
                Category.newCategory("Kids", "Categoria para criancas", true),
                Category.newCategory("Series", null, true)
        ).map(CategoryJpaEntity::from).toList();

        categoryRepository.saveAllAndFlush(categories);
    }

    @ParameterizedTest
    @CsvSource({
            "fil, 0, 10, 1, 1, Filmes",
            "net, 0, 10, 1, 1, Netflix Originals",
            "ZON, 0, 10, 1, 1, Amazon Originals",
            "kI, 0, 10, 1, 1, Kids",
            "criancas, 0, 10, 1, 1, Kids",
            "da Amazon, 0, 10, 1, 1, Amazon Originals"
    })
    public void givenAValidTerm_whencallsListCategories_ShouldReturnCategoriesFiltered(
            final String expectedTerms,
            final int expectedPage,
            final int expectedPerPage,
            final int expectedItemsCount,
            final long expectedTotal,
            final String expectedCategoryName
    ) {
        final var expectedSort = "description";
        final var expectedDirection = "asc";

        final var aQuery =
                new SearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        final var actualResult = useCase.execute(aQuery);

        Assertions.assertEquals(expectedItemsCount, actualResult.items().size());
        Assertions.assertEquals(expectedPage, actualResult.currentPage());
        Assertions.assertEquals(expectedPerPage, actualResult.perPage());
        Assertions.assertEquals(expectedTotal, actualResult.total());
        Assertions.assertEquals(expectedCategoryName, actualResult.items().get(0).name());
    }

    @ParameterizedTest
    @CsvSource({
            "name, asc, 0, 10, 7, 7, Amazon Originals",
            "name, desc, 0, 10, 7, 7, Sports",
            "createdAt, asc, 0, 10, 7, 7, Filmes",
            "createdAt, desc, 0, 10, 7, 7, Series",
    })
    public void givenAValidSortAndDirection_whencallsListCategories_ShouldReturnCategoriesFiltered(
            final String expectedSort,
            final String expectedDirection,
            final int expectedPage,
            final int expectedPerPage,
            final int expectedItemsCount,
            final long expectedTotal,
            final String expectedCategoryName
    ) {
        final var expectedTerms = "";

        final var aQuery =
                new SearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        final var actualResult = useCase.execute(aQuery);

        Assertions.assertEquals(expectedItemsCount, actualResult.items().size());
        Assertions.assertEquals(expectedPage, actualResult.currentPage());
        Assertions.assertEquals(expectedPerPage, actualResult.perPage());
        Assertions.assertEquals(expectedTotal, actualResult.total());
        Assertions.assertEquals(expectedCategoryName, actualResult.items().get(0).name());
    }

    @ParameterizedTest
    @CsvSource({
            "0, 2, 2, 7, Amazon Originals;Documentários",
            "1, 2, 2, 7, Filmes;Kids",
            "2, 2, 2, 7, Netflix Originals;Series",
            "3, 2, 1, 7, Sports",
    })
    public void givenAValidPage_whencallsListCategories_ShouldReturnCategoriesPaginated(
            final int expectedPage,
            final int expectedPerPage,
            final int expectedItemsCount,
            final long expectedTotal,
            final String expectedCategoriesName
    ) {
        final var expectedSort = "name";
        final var expectedDirection = "asc";
        final var expectedTerms = "";

        final var aQuery =
                new SearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        final var actualResult = useCase.execute(aQuery);

        Assertions.assertEquals(expectedItemsCount, actualResult.items().size());
        Assertions.assertEquals(expectedPage, actualResult.currentPage());
        Assertions.assertEquals(expectedPerPage, actualResult.perPage());
        Assertions.assertEquals(expectedTotal, actualResult.total());

        int index = 0;
        for (final String expectedName : expectedCategoriesName.split(";")) {
            Assertions.assertEquals(expectedName, actualResult.items().get(index).name());
            index++;
        }

    }

}
