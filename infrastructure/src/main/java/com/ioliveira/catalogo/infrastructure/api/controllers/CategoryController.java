package com.ioliveira.catalogo.infrastructure.api.controllers;

import com.ioliveira.catalogo.application.category.create.CreateCategoryCommand;
import com.ioliveira.catalogo.application.category.create.CreateCategoryOutput;
import com.ioliveira.catalogo.application.category.create.CreateCategoryUseCase;
import com.ioliveira.catalogo.application.category.delete.DeleteCategoryUseCase;
import com.ioliveira.catalogo.application.category.retrieve.get.GetCategoryByIdUsecase;
import com.ioliveira.catalogo.application.category.retrieve.list.ListCategoriesUseCase;
import com.ioliveira.catalogo.application.category.update.UpdateCategoryCommand;
import com.ioliveira.catalogo.application.category.update.UpdateCategoryOutput;
import com.ioliveira.catalogo.application.category.update.UpdateCategoryUseCase;
import com.ioliveira.catalogo.domain.pagination.SearchQuery;
import com.ioliveira.catalogo.domain.pagination.Pagination;
import com.ioliveira.catalogo.domain.validation.handler.Notification;
import com.ioliveira.catalogo.infrastructure.api.CategoryAPI;
import com.ioliveira.catalogo.infrastructure.category.models.CategoryListResponse;
import com.ioliveira.catalogo.infrastructure.category.models.CreateCategoryRequest;
import com.ioliveira.catalogo.infrastructure.category.models.CategoryResponse;
import com.ioliveira.catalogo.infrastructure.category.models.UpdateCategoryRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Objects;
import java.util.function.Function;

@RestController
public class CategoryController implements CategoryAPI {

    private final CreateCategoryUseCase createCategoryUseCase;
    private final GetCategoryByIdUsecase getCategoryByIdUsecase;
    private final UpdateCategoryUseCase updateCategoryUseCase;
    private final DeleteCategoryUseCase deleteCategoryUseCase;
    private final ListCategoriesUseCase listCategoriesUseCase;

    public CategoryController(final CreateCategoryUseCase createCategoryUseCase,
                              final GetCategoryByIdUsecase getCategoryByIdUsecase,
                              final UpdateCategoryUseCase updateCategoryUseCase,
                              final DeleteCategoryUseCase deleteCategoryUseCase,
                              final ListCategoriesUseCase listCategoriesUseCase) {
        this.createCategoryUseCase = Objects.requireNonNull(createCategoryUseCase);
        this.getCategoryByIdUsecase = Objects.requireNonNull(getCategoryByIdUsecase);
        this.updateCategoryUseCase = Objects.requireNonNull(updateCategoryUseCase);
        this.deleteCategoryUseCase = Objects.requireNonNull(deleteCategoryUseCase);
        this.listCategoriesUseCase = Objects.requireNonNull(listCategoriesUseCase);
    }

    @Override
    public ResponseEntity<?> createCategory(final CreateCategoryRequest input) {

        final CreateCategoryCommand command = CreateCategoryCommand.with(
                input.name(),
                input.description(),
                input.active() != null ? input.active() : true);

        final Function<Notification, ResponseEntity<?>> onError =
                notification -> ResponseEntity.unprocessableEntity().body(notification);

        final Function<CreateCategoryOutput, ResponseEntity<?>> onSuccess =
                output -> ResponseEntity.created(URI.create("/categories/" + output.id())).body(output);

        return this.createCategoryUseCase.execute(command).fold(onError, onSuccess);
    }

    @Override
    public Pagination<CategoryListResponse> listCategories(final String search, final int page,
                                                           final int perPage, final String sort, final String direction) {

        return this.listCategoriesUseCase
                .execute(new SearchQuery(page, perPage, search, sort, direction))
                .map(CategoryListResponse::from);
    }

    @Override
    public CategoryResponse findById(final String id) {
        return CategoryResponse.from(this.getCategoryByIdUsecase.execute(id));
    }

    @Override
    public ResponseEntity<?> updateById(final String id, final UpdateCategoryRequest input) {

        final UpdateCategoryCommand command = UpdateCategoryCommand.with(
                id,
                input.name(),
                input.description(),
                input.active() != null ? input.active() : true);

        final Function<Notification, ResponseEntity<?>> onError =
                notification -> ResponseEntity.unprocessableEntity().body(notification);

        final Function<UpdateCategoryOutput, ResponseEntity<?>> onSuccess =
                updateCategoryOutput -> ResponseEntity.ok(UpdateCategoryOutput.from(id));

        return this.updateCategoryUseCase.execute(command).fold(onError, onSuccess);
    }

    @Override
    public void deleteById(final String id) {
        this.deleteCategoryUseCase.execute(id);
    }
}
