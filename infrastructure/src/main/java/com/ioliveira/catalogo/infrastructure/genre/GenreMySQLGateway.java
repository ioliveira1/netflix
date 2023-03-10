package com.ioliveira.catalogo.infrastructure.genre;

import com.ioliveira.catalogo.domain.genre.Genre;
import com.ioliveira.catalogo.domain.genre.GenreGateway;
import com.ioliveira.catalogo.domain.genre.GenreID;
import com.ioliveira.catalogo.domain.pagination.Pagination;
import com.ioliveira.catalogo.domain.pagination.SearchQuery;
import com.ioliveira.catalogo.infrastructure.genre.persistence.GenreJpaEntity;
import com.ioliveira.catalogo.infrastructure.genre.persistence.GenreRepository;
import com.ioliveira.catalogo.infrastructure.utils.SpecificationUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

@Component
public class GenreMySQLGateway implements GenreGateway {

    private final GenreRepository genreRepository;

    public GenreMySQLGateway(final GenreRepository genreRepository) {
        this.genreRepository = Objects.requireNonNull(genreRepository);
    }

    @Override
    public Genre create(final Genre genre) {
        return save(genre);
    }

    @Override
    public void deleteById(final GenreID id) {
        final String genreId = id.getValue();
        if (this.genreRepository.existsById(genreId)) {
            this.genreRepository.deleteById(genreId);
        }
    }

    @Override
    public Optional<Genre> findById(final GenreID id) {
        return this.genreRepository
                .findById(id.getValue())
                .map(GenreJpaEntity::toAggregate);
    }

    @Override
    public Genre update(final Genre genre) {
        return save(genre);
    }

    @Override
    public Pagination<Genre> findAll(final SearchQuery query) {
        final PageRequest page = PageRequest.of(
                query.page(),
                query.perPage(),
                Sort.Direction.valueOf(query.direction().toUpperCase()),
                query.sort()
        );

        final Specification<GenreJpaEntity> specifications = Optional.ofNullable(query.terms())
                .filter(str -> !str.isBlank())
                .map(str -> SpecificationUtils.<GenreJpaEntity>like("name", str))
                .orElse(null);

        final Page<GenreJpaEntity> pageResult = this.genreRepository.findAll(specifications, page);

        return new Pagination<>(
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.map(GenreJpaEntity::toAggregate).toList());
    }

    private Genre save(final Genre genre) {
        return this.genreRepository
                .save(GenreJpaEntity.from(genre))
                .toAggregate();
    }
}
