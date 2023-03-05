package com.ioliveira.catalogo.domain.genre;

import com.ioliveira.catalogo.domain.AggregateRoot;
import com.ioliveira.catalogo.domain.category.CategoryID;
import com.ioliveira.catalogo.domain.exceptions.NotificationException;
import com.ioliveira.catalogo.domain.utils.InstantUtils;
import com.ioliveira.catalogo.domain.validation.ValidationHandler;
import com.ioliveira.catalogo.domain.validation.handler.Notification;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Genre extends AggregateRoot<GenreID> {
    private String name;
    private boolean active;
    private List<CategoryID> categories;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;

    public Genre(final GenreID genreID,
                 final String name,
                 final boolean active,
                 final List<CategoryID> categories,
                 final Instant createdAt,
                 final Instant updatedAt,
                 final Instant deletedAt) {
        super(genreID);
        this.name = name;
        this.active = active;
        this.categories = categories;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;

        selfValidate();
    }

    public static Genre newGenre(final String name, final boolean isActive) {
        final GenreID id = GenreID.unique();
        final Instant now = InstantUtils.now();
        final Instant deletedAt = isActive ? null : now;

        return with(id, name, isActive, new ArrayList<>(), now, now, deletedAt);
    }

    public static Genre with(final GenreID genreID,
                             final String name,
                             final boolean active,
                             final List<CategoryID> categories,
                             final Instant createdAt,
                             final Instant updatedAt,
                             final Instant deletedAt) {

        return new Genre(genreID, name, active, categories, createdAt, updatedAt, deletedAt);
    }

    public static Genre with(final Genre genre) {
        return new Genre(
                genre.id,
                genre.getName(),
                genre.isActive(),
                new ArrayList<>(genre.categories),
                genre.createdAt,
                genre.updatedAt,
                genre.deletedAt
        );
    }

    public Genre deactivate() {
        if (getDeletedAt() == null) {
            this.deletedAt = InstantUtils.now();
        }
        this.active = false;
        this.updatedAt = InstantUtils.now();
        return this;
    }

    public Genre activate() {
        this.deletedAt = null;
        this.active = true;
        this.updatedAt = InstantUtils.now();
        return this;
    }

    public Genre update(final String name, final boolean active, final List<CategoryID> categories) {
        if (active) {
            activate();
        } else {
            deactivate();
        }
        this.name = name;
        this.categories = new ArrayList<>(isNullOrEmpty(categories) ? Collections.emptyList() : categories);
        this.updatedAt = InstantUtils.now();
        selfValidate();
        return this;
    }

    public Genre addCategory(final CategoryID categoryID) {
        if (categoryID == null) {
            return this;
        }
        this.categories.add(categoryID);
        this.updatedAt = InstantUtils.now();
        return this;
    }

    public Genre addCategories(final List<CategoryID> categories) {
        if (categories == null || categories.isEmpty()) {
            return this;
        }
        this.categories.addAll(categories);
        this.updatedAt = InstantUtils.now();
        return this;
    }

    public Genre removeCategory(final CategoryID categoryID) {
        if (categoryID == null) {
            return this;
        }
        this.categories.remove(categoryID);
        this.updatedAt = InstantUtils.now();
        return this;
    }

    private boolean isNullOrEmpty(final Collection<?> collections) {
        return collections == null || collections.isEmpty();
    }

    @Override
    public void validate(final ValidationHandler handler) {
        new GenreValidator(this, handler).validate();
    }

    private void selfValidate() {
        final Notification notification = Notification.create();
        validate(notification);

        if (notification.hasErrors()) {
            throw new NotificationException(notification);
        }
    }

    public String getName() {
        return name;
    }

    public List<CategoryID> getCategories() {
        return Collections.unmodifiableList(categories);
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Instant getDeletedAt() {
        return deletedAt;
    }

    public boolean isActive() {
        return active;
    }
}
