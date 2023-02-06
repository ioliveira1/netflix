package com.ioliveira.catalogo.domain.category;

import com.ioliveira.catalogo.domain.AggregateRoot;

import java.time.Instant;

public class Category extends AggregateRoot<CategoryID> {

    private String name;
    private String description;
    private boolean active;
    private Instant createdAt;
    private Instant UpdatedAt;
    private Instant deletedAt;

    private Category(final CategoryID id, final String name, final String description, final boolean active,
                    final Instant createdAt, final Instant updatedAt, final Instant deletedAt) {

        super(id);
        this.name = name;
        this.description = description;
        this.active = active;
        this.createdAt = createdAt;
        UpdatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

    public static Category newCategory(final String name, final String description, final boolean isActive) {
        final CategoryID id = CategoryID.unique();
        final Instant now = Instant.now();

        return new Category(id, name, description, isActive, now, now, null);
    }

    public CategoryID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return active;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return UpdatedAt;
    }

    public Instant getDeletedAt() {
        return deletedAt;
    }
}
