package com.ioliveira.catalogo.infrastructure.configuration.usecases;

import com.ioliveira.catalogo.application.category.retrieve.list.DefaultListGenreUseCase;
import com.ioliveira.catalogo.application.genre.create.CreateGenreUseCase;
import com.ioliveira.catalogo.application.genre.create.DefaultCreateGenreUseCase;
import com.ioliveira.catalogo.application.genre.delete.DefaultDeleteGenreUseCase;
import com.ioliveira.catalogo.application.genre.delete.DeleteGenreUseCase;
import com.ioliveira.catalogo.application.genre.retrieve.get.DefaultGetGenreByIdUseCase;
import com.ioliveira.catalogo.application.genre.retrieve.get.GetGenreByIdUseCase;
import com.ioliveira.catalogo.application.genre.retrieve.list.ListGenreUseCase;
import com.ioliveira.catalogo.application.genre.update.DefaultUpdateGenreUseCase;
import com.ioliveira.catalogo.application.genre.update.UpdateGenreUseCase;
import com.ioliveira.catalogo.domain.category.CategoryGateway;
import com.ioliveira.catalogo.domain.genre.GenreGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

@Configuration
public class GenreUseCaseConfig {

    private final GenreGateway genreGateway;
    private final CategoryGateway categoryGateway;

    public GenreUseCaseConfig(final CategoryGateway categoryGateway, final GenreGateway genreGateway) {
        this.categoryGateway = Objects.requireNonNull(categoryGateway);
        this.genreGateway = Objects.requireNonNull(genreGateway);
    }

    @Bean
    public CreateGenreUseCase createGenreUseCase() {
        return new DefaultCreateGenreUseCase(categoryGateway, genreGateway);
    }

    @Bean
    public DeleteGenreUseCase deleteGenreUseCase() {
        return new DefaultDeleteGenreUseCase(genreGateway);
    }

    @Bean
    public GetGenreByIdUseCase getGenreByIdUseCase() {
        return new DefaultGetGenreByIdUseCase(genreGateway);
    }

    @Bean
    public ListGenreUseCase listGenreUseCase() {
        return new DefaultListGenreUseCase(genreGateway);
    }

    @Bean
    public UpdateGenreUseCase updateGenreUseCase() {
        return new DefaultUpdateGenreUseCase(genreGateway, categoryGateway);
    }
}
