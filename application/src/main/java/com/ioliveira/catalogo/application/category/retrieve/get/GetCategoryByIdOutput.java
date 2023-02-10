package com.ioliveira.catalogo.application.category.retrieve.get;

import com.ioliveira.catalogo.domain.category.Category;
import com.ioliveira.catalogo.domain.category.CategoryID;

import java.time.Instant;

public record GetCategoryByIdOutput(CategoryID id, String name, String description, boolean isActive,
                                    Instant createdAt, Instant UpdatedAt, Instant deletedAt) {

    public static GetCategoryByIdOutput with(Category category) {
        return new GetCategoryByIdOutput(category.getId(), category.getName(), category.getDescription(),
                category.isActive(), category.getCreatedAt(), category.getUpdatedAt(), category.getDeletedAt());
    }
}