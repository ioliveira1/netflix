package com.ioliveira.catalogo.application.category.retrieve.list;

import com.ioliveira.catalogo.domain.category.CategoryGateway;
import com.ioliveira.catalogo.domain.category.CategorySearchQuery;
import com.ioliveira.catalogo.domain.pagination.Pagination;

import java.util.Objects;

public class DefaultListCategoriesUseCase extends ListCategoriesUseCase {

    private final CategoryGateway categoryGateway;

    public DefaultListCategoriesUseCase(final CategoryGateway categoryGateway) {
        this.categoryGateway = Objects.requireNonNull(categoryGateway);
    }

    @Override
    public Pagination<CategoryListOutput> execute(final CategorySearchQuery query) {

        return this.categoryGateway.findAll(query)
                .map(CategoryListOutput::from);
    }
}
