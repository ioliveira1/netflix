package com.ioliveira.catalogo.infrastructure.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ioliveira.catalogo.ControllerTest;
import com.ioliveira.catalogo.application.genre.create.CreateGenreOutput;
import com.ioliveira.catalogo.application.genre.create.CreateGenreUseCase;
import com.ioliveira.catalogo.application.genre.delete.DeleteGenreUseCase;
import com.ioliveira.catalogo.application.genre.retrieve.get.GenreOutput;
import com.ioliveira.catalogo.application.genre.retrieve.get.GetGenreByIdUseCase;
import com.ioliveira.catalogo.application.genre.retrieve.list.GenreListOutput;
import com.ioliveira.catalogo.application.genre.retrieve.list.ListGenreUseCase;
import com.ioliveira.catalogo.application.genre.update.UpdateGenreOutput;
import com.ioliveira.catalogo.application.genre.update.UpdateGenreUseCase;
import com.ioliveira.catalogo.domain.category.CategoryID;
import com.ioliveira.catalogo.domain.exceptions.NotFoundException;
import com.ioliveira.catalogo.domain.exceptions.NotificationException;
import com.ioliveira.catalogo.domain.genre.Genre;
import com.ioliveira.catalogo.domain.genre.GenreID;
import com.ioliveira.catalogo.domain.pagination.Pagination;
import com.ioliveira.catalogo.domain.validation.Error;
import com.ioliveira.catalogo.infrastructure.genre.models.CreateGenreRequest;
import com.ioliveira.catalogo.infrastructure.genre.models.UpdateGenreRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

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

@ControllerTest(controllers = GenreApi.class)
public class GenreApiTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private CreateGenreUseCase createGenreUseCase;

    @MockBean
    private GetGenreByIdUseCase getGenreByIdUseCase;

    @MockBean
    private UpdateGenreUseCase updateGenreUseCase;

    @MockBean
    private DeleteGenreUseCase deleteGenreUseCase;

    @MockBean
    private ListGenreUseCase listGenreUseCase;

    @Test
    public void givenAValidCommand_whenCallsCreateGenre_thenShouldReturnCategoryId() throws Exception {
        final var expectedName = "Drama";
        final var categories = List.of("123", "456");
        final var expectedIsActive = true;
        final var expectedId = "789";

        final var input = new CreateGenreRequest(expectedName, categories, expectedIsActive);

        when(createGenreUseCase.execute(any()))
                .thenReturn(CreateGenreOutput.from(expectedId));

        final var request = post("/genres")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(input));

        this.mvc.perform(request)
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/genres/" + expectedId))
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id", equalTo(expectedId)));
    }

    @Test
    public void givenAnInvalidName_whenCallsCreateGenre_thenShouldReturnDomainException() throws Exception {
        final var categories = List.of("123", "456");
        final var expectedIsActive = true;
        final var expectedId = "789";
        final var expectedMessage = "'name' should not be null";

        final var input = new CreateGenreRequest(null, categories, expectedIsActive);

        when(createGenreUseCase.execute(any()))
                .thenThrow(NotificationException.with(new Error(expectedMessage)));

        final var request = post("/genres")
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
    public void givenAValidId_whenCallsGetGenreById_thenShouldReturnGenre() throws Exception {
        final var expectedName = "Drama";
        final var expectedIsActive = true;
        final var expectedCategories = List.of("123", "456");

        final var genre = Genre.newGenre(expectedName, expectedIsActive);
        genre.addCategories(expectedCategories.stream().map(CategoryID::from).toList());

        final var expectedId = genre.getId().getValue();

        when(getGenreByIdUseCase.execute(any()))
                .thenReturn(GenreOutput.from(genre));

        final var request = get("/genres/{id}", expectedId);

        this.mvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(expectedId)))
                .andExpect(jsonPath("$.name", equalTo(expectedName)))
                .andExpect(jsonPath("$.categories_id", equalTo(expectedCategories)))
                .andExpect(jsonPath("$.is_active", equalTo(expectedIsActive)))
                .andExpect(jsonPath("$.createdAt", equalTo(genre.getCreatedAt().toString())))
                .andExpect(jsonPath("$.updatedAt", equalTo(genre.getUpdatedAt().toString())))
                .andExpect(jsonPath("$.deletedAt", equalTo(genre.getDeletedAt())));
    }

    @Test
    public void givenAnInValidId_whenCallsGetGenre_thenShouldReturnNotFound() throws Exception {
        final var expectedErrorMessage = "Genre with ID 123 was not found";
        final var expectedId = GenreID.from("123").getValue();

        when(getGenreByIdUseCase.execute(any()))
                .thenThrow(NotFoundException.with(new Error(expectedErrorMessage)));

        final var request = get("/genres/{id}", expectedId);

        this.mvc.perform(request)
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors[0].message", equalTo(expectedErrorMessage)));
    }

    @Test
    public void givenAValidCommand_whenCallsUpdateGenre_thenShouldReturnGenreId() throws Exception {
        final var expectedName = "Drama";
        final var expectedIsActive = true;
        final var expectedCategories = List.of("123", "456");

        final Genre genre = Genre.newGenre(expectedName, expectedIsActive);
        final var expectedId = genre.getId().getValue();

        when(updateGenreUseCase.execute(any()))
                .thenReturn(UpdateGenreOutput.from(genre));

        final var input = new UpdateGenreRequest(expectedName, expectedCategories, expectedIsActive);

        final var request = put("/genres/{id}", expectedId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(input));

        this.mvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(expectedId)));
    }

    @Test
    public void givenAnInvalidId_whenCallsUpdateGenre_thenShouldException() throws Exception {
        final var expectedId = "asdf";
        final var expectedIsActive = true;
        final var expectedCategories = List.of("123", "456");

        final var expectedErrorMessage = "Category with ID asdf not found";
        final var expectedErrorCountMessage = 1;

        when(updateGenreUseCase.execute(any()))
                .thenThrow(NotFoundException.with(new Error(expectedErrorMessage)));

        final var input = new UpdateGenreRequest(null, expectedCategories, expectedIsActive);

        final var request = put("/genres/{id}", expectedId)
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
    public void givenAValidId_whenCallsDeleteGenre_thenShouldReturnNoContent() throws Exception {
        final var expectedId = "123";

        doNothing().when(deleteGenreUseCase).execute(any());

        final var request = delete("/genres/{id}", expectedId);

        this.mvc.perform(request)
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    public void givenValidParams_whenCallsListGenres_thenShouldReturnGenres() throws Exception {
        final var genre = Genre.newGenre("Drama", true);

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "dr";
        final var expectedSort = "name";
        final var expectedDirection = "asc";
        final var expectedItemsCount = 1;
        final var expectedTotal = 1;
        final var expectedItems = List.of(GenreListOutput.from(genre));

        when(listGenreUseCase.execute(any()))
                .thenReturn(new Pagination<>(expectedPage, expectedPage, expectedTotal, expectedItems));

        final var request = get("/genres")
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
                .andExpect(jsonPath("$.items[0].id", equalTo(genre.getId().getValue())))
                .andExpect(jsonPath("$.items[0].name", equalTo(genre.getName())))
                .andExpect(jsonPath("$.items[0].is_active", equalTo(genre.isActive())))
                .andExpect(jsonPath("$.items[0].createdAt", equalTo(genre.getCreatedAt().toString())))
                .andExpect(jsonPath("$.items[0].deletedAt", equalTo(genre.getDeletedAt())));
    }
}
