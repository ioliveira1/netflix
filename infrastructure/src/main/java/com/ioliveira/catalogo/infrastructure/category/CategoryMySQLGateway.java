package com.ioliveira.catalogo.infrastructure.category;

import com.ioliveira.catalogo.domain.category.Category;
import com.ioliveira.catalogo.domain.category.CategoryGateway;
import com.ioliveira.catalogo.domain.category.CategoryID;
import com.ioliveira.catalogo.domain.category.CategorySearchQuery;
import com.ioliveira.catalogo.domain.pagination.Pagination;
import com.ioliveira.catalogo.infrastructure.category.persistence.CategoryJpaEntity;
import com.ioliveira.catalogo.infrastructure.category.persistence.CategoryRepository;
import com.ioliveira.catalogo.infrastructure.utils.SpecificationUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CategoryMySQLGateway implements CategoryGateway {

    private final CategoryRepository repository;

    public CategoryMySQLGateway(final CategoryRepository repository) {
        this.repository = repository;
    }

    @Override
    public Category create(final Category category) {
        return this.repository
                .save(CategoryJpaEntity.from(category))
                .toAggregate();
    }

    @Override
    public void deleteById(final CategoryID id) {
        final String categoryId = id.getValue();
        if (this.repository.existsById(categoryId)) {
            this.repository.deleteById(categoryId);
        }
    }

    @Override
    public Optional<Category> findById(final CategoryID id) {
        return this.repository
                .findById(id.getValue())
                .map(CategoryJpaEntity::toAggregate);
    }

    @Override
    public Category update(final Category category) {
        return this.repository
                .save(CategoryJpaEntity.from(category))
                .toAggregate();
    }

    @Override
    public Pagination<Category> findAll(final CategorySearchQuery query) {
        final PageRequest page = PageRequest.of(query.page(),
                query.perPage(),
                Sort.Direction.valueOf(query.direction().toUpperCase()),
                query.sort()
        );

        final Specification<CategoryJpaEntity> specifications = Optional.ofNullable(query.terms())
                .filter(str -> !str.isBlank())
                .map(str -> SpecificationUtils.<CategoryJpaEntity>like(query.sort(), str))
                .orElse(null);

        final Page<CategoryJpaEntity> pageResult = this.repository.findAll(specifications, page);

        return new Pagination<>(
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.map(CategoryJpaEntity::toAggregate).toList());
    }
}
