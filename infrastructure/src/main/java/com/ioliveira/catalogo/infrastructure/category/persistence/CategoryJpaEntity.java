package com.ioliveira.catalogo.infrastructure.category.persistence;

import com.ioliveira.catalogo.domain.category.Category;
import com.ioliveira.catalogo.domain.category.CategoryID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.Instant;

@Table(name = "category")
@Entity
public class CategoryJpaEntity {
    @Id
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", length = 4000)
    private String description;

    @Column(name = "active", nullable = false)
    private boolean active;

    @Column(name = "created_at", nullable = false, columnDefinition = "DATETIME(6)")
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false, columnDefinition = "DATETIME(6)")
    private Instant updatedAt;

    @Column(name = "deleted_at", columnDefinition = "DATETIME(6)")
    private Instant deletedAt;

    public CategoryJpaEntity() {
    }

    private CategoryJpaEntity(final String id, final String name, final String description, final boolean active,
                              final Instant createdAt, final Instant updatedAt, final Instant deletedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

    public static CategoryJpaEntity from(Category category) {
        return new CategoryJpaEntity(category.getId().getValue(), category.getName(), category.getDescription(),
                category.isActive(), category.getCreatedAt(), category.getUpdatedAt(), category.getDeletedAt());
    }

    public Category toAggregate() {
        return Category.with(CategoryID.from(getId()), getName(), getDescription(), getActive(),
                getCreatedAt(), getUpdatedAt(), getDeletedAt());
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public boolean getActive() {
        return active;
    }

    public void setActive(final boolean active) {
        this.active = active;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(final Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(final Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Instant getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(final Instant deletedAt) {
        this.deletedAt = deletedAt;
    }
}
