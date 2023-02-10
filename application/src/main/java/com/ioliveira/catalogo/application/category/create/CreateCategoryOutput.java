package com.ioliveira.catalogo.application.category.create;

import com.ioliveira.catalogo.domain.category.Category;
import com.ioliveira.catalogo.domain.category.CategoryID;

public record CreateCategoryOutput(CategoryID id) {

    public static CreateCategoryOutput from(final Category category) {
        return new CreateCategoryOutput(category.getId());
    }
}
