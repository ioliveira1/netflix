package com.ioliveira.catalogo.infrastructure.category;

import com.ioliveira.catalogo.MySQLGatewayTest;
import com.ioliveira.catalogo.domain.category.Category;
import com.ioliveira.catalogo.domain.category.CategoryGateway;
import com.ioliveira.catalogo.domain.category.CategoryID;
import com.ioliveira.catalogo.domain.pagination.SearchQuery;
import com.ioliveira.catalogo.infrastructure.category.persistence.CategoryJpaEntity;
import com.ioliveira.catalogo.infrastructure.category.persistence.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@MySQLGatewayTest
public class CategoryMySQLGatewayTest {

    @Autowired
    private CategoryGateway categoryGateway;

    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeEach
    public void cleanUp() {
        this.categoryRepository.deleteAll();
    }

    @Test
    public void givenAValidCategory_whenCallsCreate_ShouldReturANewCategory() {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var category = Category.newCategory(expectedName, expectedDescription, expectedIsActive);

        assertEquals(0, categoryRepository.count());

        final var categoryCreated = categoryGateway.create(category);

        assertEquals(1, categoryRepository.count());
        assertEquals(category.getId(), categoryCreated.getId());
        assertEquals(expectedName, categoryCreated.getName());
        assertEquals(expectedDescription, categoryCreated.getDescription());
        assertEquals(expectedIsActive, categoryCreated.isActive());
        assertEquals(category.getCreatedAt(), categoryCreated.getCreatedAt());
        assertEquals(category.getUpdatedAt(), categoryCreated.getUpdatedAt());
        assertEquals(category.getDeletedAt(), categoryCreated.getDeletedAt());
        assertNull(categoryCreated.getDeletedAt());

        final var entity = categoryRepository.findById(category.getId().getValue()).get();

        assertEquals(category.getId().getValue(), entity.getId());
        assertEquals(expectedName, entity.getName());
        assertEquals(expectedDescription, entity.getDescription());
        assertEquals(expectedIsActive, entity.getActive());
        assertEquals(category.getCreatedAt(), entity.getCreatedAt());
        assertEquals(category.getUpdatedAt(), entity.getUpdatedAt());
        assertEquals(category.getDeletedAt(), entity.getDeletedAt());
        assertNull(entity.getDeletedAt());
    }

    @Test
    public void givenAValidCategory_whenCallsUpdate_ShouldReturACategoryUpdated() {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var category = Category.newCategory("Film", null, expectedIsActive);

        assertEquals(0, categoryRepository.count());

        categoryRepository.saveAndFlush(CategoryJpaEntity.from(category));

        assertEquals(1, categoryRepository.count());

        final var categoryUpdated = category.clone().update(expectedName, expectedDescription, expectedIsActive);

        final var categoryCreated = categoryGateway.update(categoryUpdated);

        assertEquals(1, categoryRepository.count());
        assertEquals(category.getId(), categoryCreated.getId());
        assertEquals(expectedName, categoryCreated.getName());
        assertEquals(expectedDescription, categoryCreated.getDescription());
        assertEquals(expectedIsActive, categoryCreated.isActive());
        assertEquals(category.getCreatedAt(), categoryCreated.getCreatedAt());
        assertTrue(category.getUpdatedAt().isBefore(categoryCreated.getUpdatedAt()));
        assertEquals(category.getDeletedAt(), categoryCreated.getDeletedAt());
        assertNull(categoryCreated.getDeletedAt());

        final var entity = categoryRepository.findById(category.getId().getValue()).get();

        assertEquals(category.getId().getValue(), entity.getId());
        assertEquals(expectedName, entity.getName());
        assertEquals(expectedDescription, entity.getDescription());
        assertEquals(expectedIsActive, entity.getActive());
        assertTrue(category.getUpdatedAt().isBefore(categoryCreated.getUpdatedAt()));
        assertEquals(category.getDeletedAt(), entity.getDeletedAt());
        assertNull(entity.getDeletedAt());
    }

    @Test
    public void givenAValidCategoryId_whenCallsDelete_ShouldReturDeleteCategory() {
        final var category = Category.newCategory("Filmes", "A categoria mais assistida", true);

        assertEquals(0, categoryRepository.count());

        categoryRepository.saveAndFlush(CategoryJpaEntity.from(category)).toAggregate();

        assertEquals(1, categoryRepository.count());

        categoryGateway.deleteById(category.getId());

        assertEquals(0, categoryRepository.count());
    }

    @Test
    public void givenAnInvalidCategoryId_whenCallsDelete_ShouldReturDeleteCategory() {
        final var category = Category.newCategory("Filmes", "A categoria mais assistida", true);

        assertEquals(0, categoryRepository.count());

        categoryRepository.saveAndFlush(CategoryJpaEntity.from(category)).toAggregate();

        assertEquals(1, categoryRepository.count());

        categoryGateway.deleteById(CategoryID.from("123"));

        assertEquals(1, categoryRepository.count());
    }

    @Test
    public void givenAValidCategory_whenCallsFindById_ShouldReturACategory() {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var category = Category.newCategory(expectedName, expectedDescription, expectedIsActive);

        assertEquals(0, categoryRepository.count());

        categoryRepository.saveAndFlush(CategoryJpaEntity.from(category));

        assertEquals(1, categoryRepository.count());

        final var categoryDB = categoryGateway.findById(category.getId()).get();

        assertEquals(1, categoryRepository.count());
        assertEquals(category.getId(), categoryDB.getId());
        assertEquals(expectedName, categoryDB.getName());
        assertEquals(expectedDescription, categoryDB.getDescription());
        assertEquals(expectedIsActive, categoryDB.isActive());
        assertEquals(category.getCreatedAt(), categoryDB.getCreatedAt());
        assertEquals(category.getUpdatedAt(), categoryDB.getUpdatedAt());
        assertEquals(category.getDeletedAt(), categoryDB.getDeletedAt());
        assertNull(categoryDB.getDeletedAt());
    }

    @Test
    public void givenPrepersistedCategories_whenCallsFindAll_ShouldReturPaginated() {
        final var expectedPage = 0;
        final var expectedPerPage = 1;
        final var expectedTotal = 3;

        final var filmes = Category.newCategory("Filmes", null, true);
        final var series = Category.newCategory("S??ries", null, true);
        final var documentarios = Category.newCategory("Document??rios", null, true);

        assertEquals(0, categoryRepository.count());

        categoryRepository
                .saveAll(List.of(CategoryJpaEntity.from(filmes),
                        CategoryJpaEntity.from(series),
                        CategoryJpaEntity.from(documentarios))
                );

        assertEquals(3, categoryRepository.count());

        final var query = new SearchQuery(0, 1, "", "name", "asc");

        final var result = categoryGateway.findAll(query);

        assertEquals(expectedPage, result.currentPage());
        assertEquals(expectedPerPage, result.perPage());
        assertEquals(expectedTotal, result.total());
        assertEquals(expectedPerPage, result.items().size());
        assertEquals(documentarios.getId(), result.items().get(0).getId());
    }

    @Test
    public void givenEmptyDataBase_whenCallsFindAll_ShouldReturEmptyPage() {
        final var expectedPage = 0;
        final var expectedPerPage = 1;
        final var expectedTotal = 0;

        assertEquals(0, categoryRepository.count());

        final var query = new SearchQuery(0, 1, "", "name", "asc");

        final var result = categoryGateway.findAll(query);

        assertEquals(expectedPage, result.currentPage());
        assertEquals(expectedPerPage, result.perPage());
        assertEquals(expectedTotal, result.total());
        assertEquals(expectedPage, result.items().size());
    }

    @Test
    public void givenAPagination_whenCallsFindAllWithPage1_ShouldReturPaginated() {
        var expectedPage = 0;
        final var expectedPerPage = 1;
        final var expectedTotal = 3;

        final var filmes = Category.newCategory("Filmes", null, true);
        final var series = Category.newCategory("S??ries", null, true);
        final var documentarios = Category.newCategory("Document??rios", null, true);

        assertEquals(0, categoryRepository.count());

        categoryRepository
                .saveAll(List.of(CategoryJpaEntity.from(filmes),
                        CategoryJpaEntity.from(series),
                        CategoryJpaEntity.from(documentarios))
                );

        assertEquals(3, categoryRepository.count());

        var query = new SearchQuery(0, 1, "", "name", "asc");
        var result = categoryGateway.findAll(query);
        assertEquals(expectedPage, result.currentPage());
        assertEquals(expectedPerPage, result.perPage());
        assertEquals(expectedTotal, result.total());
        assertEquals(expectedPerPage, result.items().size());
        assertEquals(documentarios.getId(), result.items().get(0).getId());

        query = new SearchQuery(1, 1, "", "name", "asc");
        result = categoryGateway.findAll(query);
        expectedPage = 1;
        assertEquals(expectedPage, result.currentPage());
        assertEquals(expectedPerPage, result.perPage());
        assertEquals(expectedTotal, result.total());
        assertEquals(expectedPage, result.items().size());
        assertEquals(filmes.getId(), result.items().get(0).getId());

        query = new SearchQuery(2, 1, "", "name", "asc");
        result = categoryGateway.findAll(query);
        expectedPage = 2;
        assertEquals(expectedPage, result.currentPage());
        assertEquals(expectedPerPage, result.perPage());
        assertEquals(expectedTotal, result.total());
        assertEquals(expectedPerPage, result.items().size());
        assertEquals(series.getId(), result.items().get(0).getId());
    }

    @Test
    public void givenPrepersistedCategoriesAndDocAsTerm_whenCallsFindAllSortingByName_ShouldReturPaginated() {
        final var expectedPage = 0;
        final var expectedPerPage = 1;
        final var expectedTotal = 1;

        final var filmes = Category.newCategory("Filmes", null, true);
        final var series = Category.newCategory("S??ries", null, true);
        final var documentarios = Category.newCategory("Document??rios", null, true);

        assertEquals(0, categoryRepository.count());

        categoryRepository
                .saveAll(List.of(CategoryJpaEntity.from(filmes),
                        CategoryJpaEntity.from(series),
                        CategoryJpaEntity.from(documentarios))
                );

        assertEquals(3, categoryRepository.count());

        final var query = new SearchQuery(0, 1, "doc", "name", "asc");

        final var result = categoryGateway.findAll(query);

        assertEquals(expectedPage, result.currentPage());
        assertEquals(expectedPerPage, result.perPage());
        assertEquals(expectedTotal, result.total());
        assertEquals(expectedPerPage, result.items().size());
        assertEquals(documentarios.getId(), result.items().get(0).getId());
    }

    @Test
    public void givenPrepersistedCategoriesAndSucessoAsTerm_whenCallsFindAllSortingByDescription_ShouldReturPaginated() {
        final var expectedPage = 0;
        final var expectedPerPage = 1;
        final var expectedTotal = 1;

        final var filmes = Category.newCategory("Filmes", "A categoria mais assistida", true);
        final var series = Category.newCategory("S??ries", "Series de sucesso", true);
        final var documentarios = Category.newCategory("Document??rios", "Excelentes document??rios", true);

        assertEquals(0, categoryRepository.count());

        categoryRepository
                .saveAll(List.of(CategoryJpaEntity.from(filmes),
                        CategoryJpaEntity.from(series),
                        CategoryJpaEntity.from(documentarios))
                );

        assertEquals(3, categoryRepository.count());

        final var query = new SearchQuery(0, 1, "SUceSso", "description", "asc");

        final var result = categoryGateway.findAll(query);

        assertEquals(expectedPage, result.currentPage());
        assertEquals(expectedPerPage, result.perPage());
        assertEquals(expectedTotal, result.total());
        assertEquals(expectedPerPage, result.items().size());
        assertEquals(series.getId(), result.items().get(0).getId());
    }

    @Test
    public void givenPrepersistedCategories_whenCallsExistsByIds_ShouldReturIds() {
        final var filmes = Category.newCategory("Filmes", null, true);
        final var series = Category.newCategory("Series", null, true);
        final var docs = Category.newCategory("Docs", null, true);

        final var ids = List.of(filmes.getId(), series.getId(), CategoryID.from("123"));
        final var expectedIds = List.of(filmes.getId(), series.getId());

        final var categories = Stream.of(filmes, series, docs)
                .map(CategoryJpaEntity::from)
                .toList();

        assertEquals(0, categoryRepository.count());

        categoryRepository.saveAll(categories);

        assertEquals(3, categoryRepository.count());

        final var result = categoryGateway.existsByIds(ids);

        assertTrue(
                result.size() == 2
                        && expectedIds.containsAll(result)
        );
    }
}
