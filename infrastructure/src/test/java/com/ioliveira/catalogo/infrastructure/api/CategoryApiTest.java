package com.ioliveira.catalogo.infrastructure.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ioliveira.catalogo.ControllerTest;
import com.ioliveira.catalogo.application.category.create.CreateCategoryOutput;
import com.ioliveira.catalogo.application.category.create.CreateCategoryUseCase;
import com.ioliveira.catalogo.application.category.delete.DeleteCategoryUseCase;
import com.ioliveira.catalogo.application.category.retrieve.get.GetCategoryByIdOutput;
import com.ioliveira.catalogo.application.category.retrieve.get.GetCategoryByIdUsecase;
import com.ioliveira.catalogo.application.category.retrieve.list.CategoryListOutput;
import com.ioliveira.catalogo.application.category.retrieve.list.ListCategoriesUseCase;
import com.ioliveira.catalogo.application.category.update.UpdateCategoryOutput;
import com.ioliveira.catalogo.application.category.update.UpdateCategoryUseCase;
import com.ioliveira.catalogo.domain.category.Category;
import com.ioliveira.catalogo.domain.category.CategoryID;
import com.ioliveira.catalogo.domain.exceptions.DomainException;
import com.ioliveira.catalogo.domain.exceptions.NotFoundException;
import com.ioliveira.catalogo.domain.pagination.Pagination;
import com.ioliveira.catalogo.domain.validation.Error;
import com.ioliveira.catalogo.infrastructure.category.models.CreateCategoryRequest;
import com.ioliveira.catalogo.infrastructure.category.models.UpdateCategoryRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static io.vavr.API.Right;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ControllerTest(controllers = CategoryAPI.class)
public class CategoryApiTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private CreateCategoryUseCase createCategoryUseCase;

    @MockBean
    private GetCategoryByIdUsecase getCategoryByIdUsecase;

    @MockBean
    private UpdateCategoryUseCase updateCategoryUseCase;

    @MockBean
    private DeleteCategoryUseCase deleteCategoryUseCase;

    @MockBean
    private ListCategoriesUseCase listCategoriesUseCase;

    @Test
    public void givenAValidCommand_whenCallsCreateCategory_thenShouldReturnCategoryId() throws Exception {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var input = new CreateCategoryRequest(expectedName, expectedDescription, expectedIsActive);

        when(createCategoryUseCase.execute(any()))
                .thenReturn(Right(CreateCategoryOutput.from("123")));

        final var request = post("/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(input));

        this.mvc.perform(request)
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/categories/123"))
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id", equalTo("123")));

    }

    @Test
    public void givenAnInvalidName_whenCallsCreateCategory_thenShouldReturnDomainException() throws Exception {
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedMessage = "'name' should not be null";

        final var input = new CreateCategoryRequest(null, expectedDescription, expectedIsActive);

        when(createCategoryUseCase.execute(any()))
                .thenThrow(DomainException.with(new Error(expectedMessage)));

        final var request = post("/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(input));

        this.mvc.perform(request)
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(header().string("Location", nullValue()))
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].message", equalTo(expectedMessage)));
    }

    @Test
    public void givenAValidId_whenCallsGetCategory_thenShouldReturnCategory() throws Exception {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var category = Category.newCategory(expectedName, expectedDescription, expectedIsActive);
        final var expectedId = category.getId().getValue();

        when(getCategoryByIdUsecase.execute(any()))
                .thenReturn(GetCategoryByIdOutput.with(category));

        final var request = get("/categories/{id}", expectedId);

        this.mvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(expectedId)))
                .andExpect(jsonPath("$.name", equalTo(expectedName)))
                .andExpect(jsonPath("$.description", equalTo(expectedDescription)))
                .andExpect(jsonPath("$.is_active", equalTo(expectedIsActive)))
                .andExpect(jsonPath("$.createdAt", equalTo(category.getCreatedAt().toString())))
                .andExpect(jsonPath("$.updatedAt", equalTo(category.getUpdatedAt().toString())))
                .andExpect(jsonPath("$.deletedAt", equalTo(category.getDeletedAt())));
    }

    @Test
    public void givenAnInValidId_whenCallsGetCategory_thenShouldReturnNotFound() throws Exception {
        final var expectedErrorMessage = "Category with ID 123 was not found";
        final var expectedId = CategoryID.from("123").getValue();

        when(getCategoryByIdUsecase.execute(any()))
                .thenThrow(NotFoundException.with(new Error(expectedErrorMessage)));

        final var request = get("/categories/{id}", expectedId);

        this.mvc.perform(request)
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors[0].message", equalTo(expectedErrorMessage)));
    }

    @Test
    public void givenAValidCommand_whenCallsUpdateCategory_thenShouldReturnCategoryId() throws Exception {
        final var expectedId = "123";
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        when(updateCategoryUseCase.execute(any()))
                .thenReturn(Right(UpdateCategoryOutput.from(expectedId)));

        final var input = new UpdateCategoryRequest(expectedName, expectedDescription, expectedIsActive);

        final var request = put("/categories/{id}", expectedId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(input));

        this.mvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(expectedId)));
    }

    @Test
    public void givenAnInvalidId_whenCallsUpdateCategory_thenShouldException() throws Exception {
        final var expectedId = "asdf";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var expectedErrorMessage = "Category with ID asdf not found";
        final var expectedErrorCountMessage = 1;

        when(updateCategoryUseCase.execute(any()))
                .thenThrow(NotFoundException.with(new Error(expectedErrorMessage)));

        final var input = new UpdateCategoryRequest(null, expectedDescription, expectedIsActive);

        final var request = put("/categories/{id}", expectedId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(input));

        this.mvc.perform(request)
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors", hasSize(expectedErrorCountMessage)))
                .andExpect(jsonPath("$.errors[0].message", equalTo(expectedErrorMessage)));
    }

    @Test
    public void givenAnInvalidName_whenCallsUpdateCategory_thenShouldException() throws Exception {
        final var expectedId = "123";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var expectedErrorMessage = "'name' should not be null";
        final var expectedErrorCountMessage = 1;

        when(updateCategoryUseCase.execute(any()))
                .thenThrow(NotFoundException.with(new Error(expectedErrorMessage)));

        final var input = new UpdateCategoryRequest(null, expectedDescription, expectedIsActive);

        final var request = put("/categories/{id}", expectedId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(input));

        this.mvc.perform(request)
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors", hasSize(expectedErrorCountMessage)))
                .andExpect(jsonPath("$.errors[0].message", equalTo(expectedErrorMessage)));
    }

    @Test
    public void givenAValidId_whenCallsDeleteCategory_thenShouldReturnNoContent() throws Exception {
        final var expectedId = "123";

        doNothing().when(deleteCategoryUseCase).execute(any());

        final var request = delete("/categories/{id}", expectedId);

        this.mvc.perform(request)
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    public void givenValidParams_whenCallsListCategories_thenShouldReturnCategories() throws Exception {
        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "movies";
        final var expectedSort = "description";
        final var expectedDirection = "desc";
        final var expectedItemsCount = 1;
        final var expectedTotal = 1;

        final var category = Category.newCategory("Movies", null, true);
        final var expectedItems = List.of(CategoryListOutput.from(category));

        when(listCategoriesUseCase.execute(any()))
                .thenReturn(new Pagination<>(expectedPage, expectedPage, expectedTotal, expectedItems));

        final var request = get("/categories")
                .queryParam("page", String.valueOf(expectedPage))
                .queryParam("perPage", String.valueOf(expectedPerPage))
                .queryParam("sort", expectedSort)
                .queryParam("dir", expectedDirection)
                .queryParam("search", expectedTerms);

        this.mvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.current_page", equalTo(expectedPage)))
                .andExpect(jsonPath("$.per_page", equalTo(0)))
                .andExpect(jsonPath("$.total", equalTo(expectedTotal)))
                .andExpect(jsonPath("$.items", hasSize(expectedItemsCount)))
                .andExpect(jsonPath("$.items[0].id", equalTo(category.getId().getValue())))
                .andExpect(jsonPath("$.items[0].name", equalTo(category.getName())))
                .andExpect(jsonPath("$.items[0].description", equalTo(category.getDescription())))
                .andExpect(jsonPath("$.items[0].is_active", equalTo(category.isActive())))
                .andExpect(jsonPath("$.items[0].createdAt", equalTo(category.getCreatedAt().toString())))
                .andExpect(jsonPath("$.items[0].deletedAt", equalTo(category.getDeletedAt())));
    }
}
