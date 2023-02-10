package com.ioliveira.catalogo.application.category.update;

import com.ioliveira.catalogo.domain.category.Category;
import com.ioliveira.catalogo.domain.category.CategoryID;

public record UpdateCategoryOutput(CategoryID id) {

    public static UpdateCategoryOutput from(final Category category) {
        return new UpdateCategoryOutput(category.getId());
    }

}
