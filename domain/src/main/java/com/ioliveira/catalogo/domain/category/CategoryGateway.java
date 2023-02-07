package com.ioliveira.catalogo.domain.category;

import com.ioliveira.catalogo.domain.pagination.Pagination;

import java.util.Optional;

public interface CategoryGateway {

    Category create(Category category);

    void deleteById(CategoryID id);

    Optional<Category> findById(CategoryID id);

    Category update(Category category);

    Pagination<Category> findAll(CategorySearchQuery query);
}
