package com.ioliveira.catalogo.infrastructure.category.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ioliveira.catalogo.application.category.retrieve.list.CategoryListOutput;

import java.time.Instant;

public record CategoryListResponse(
        @JsonProperty("id") String id,
        @JsonProperty("name") String name,
        @JsonProperty("description") String description,
        @JsonProperty("is_active") Boolean active,
        @JsonProperty("createdAt") Instant createdAt,
        @JsonProperty("deletedAt") Instant deletedAt
) {

    public static CategoryListResponse from(final CategoryListOutput output) {
        return new CategoryListResponse(output.id().getValue(),
                output.name(),
                output.description(),
                output.isActive(),
                output.createdAt(),
                output.deletedAt());
    }

}
