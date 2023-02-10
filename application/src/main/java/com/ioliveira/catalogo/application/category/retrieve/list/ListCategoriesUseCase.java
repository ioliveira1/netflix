package com.ioliveira.catalogo.application.category.retrieve.list;

import com.ioliveira.catalogo.application.UseCase;
import com.ioliveira.catalogo.domain.category.CategorySearchQuery;
import com.ioliveira.catalogo.domain.pagination.Pagination;

public abstract class ListCategoriesUseCase extends UseCase<CategorySearchQuery, Pagination<CategoryListOutput>> {
}
