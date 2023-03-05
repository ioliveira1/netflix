package com.ioliveira.catalogo.application.category.retrieve.list;

import com.ioliveira.catalogo.application.UseCase;
import com.ioliveira.catalogo.domain.pagination.SearchQuery;
import com.ioliveira.catalogo.domain.pagination.Pagination;

public abstract class ListCategoriesUseCase extends UseCase<SearchQuery, Pagination<CategoryListOutput>> {
}
