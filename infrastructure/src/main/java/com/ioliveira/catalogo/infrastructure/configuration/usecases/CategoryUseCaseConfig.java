package com.ioliveira.catalogo.infrastructure.configuration.usecases;

import com.ioliveira.catalogo.application.category.create.CreateCategoryUseCase;
import com.ioliveira.catalogo.application.category.create.DefaultCreateCategoryUseCase;
import com.ioliveira.catalogo.application.category.delete.DefaultDeleteCategoryUseCase;
import com.ioliveira.catalogo.application.category.delete.DeleteCategoryUseCase;
import com.ioliveira.catalogo.application.category.retrieve.get.DefaultGetCategoryByIdUsecase;
import com.ioliveira.catalogo.application.category.retrieve.get.GetCategoryByIdUsecase;
import com.ioliveira.catalogo.application.category.retrieve.list.DefaultListCategoriesUseCase;
import com.ioliveira.catalogo.application.category.retrieve.list.ListCategoriesUseCase;
import com.ioliveira.catalogo.application.category.update.DefaultUpdateCategoryUseCase;
import com.ioliveira.catalogo.application.category.update.UpdateCategoryUseCase;
import com.ioliveira.catalogo.domain.category.CategoryGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CategoryUseCaseConfig {

    private final CategoryGateway categoryGateway;

    public CategoryUseCaseConfig(final CategoryGateway categoryGateway) {
        this.categoryGateway = categoryGateway;
    }

    @Bean
    public CreateCategoryUseCase defaultCreateCategoryUseCase() {
        return new DefaultCreateCategoryUseCase(categoryGateway);
    }

    @Bean
    public DeleteCategoryUseCase deleteCategoryUseCase() {
        return new DefaultDeleteCategoryUseCase(categoryGateway);
    }

    @Bean
    public GetCategoryByIdUsecase getCategoryByIdUsecase() {
        return new DefaultGetCategoryByIdUsecase(categoryGateway);
    }

    @Bean
    public ListCategoriesUseCase listCategoriesUseCase() {
        return new DefaultListCategoriesUseCase(categoryGateway);
    }

    @Bean
    public UpdateCategoryUseCase updateCategoryUseCase() {
        return new DefaultUpdateCategoryUseCase(categoryGateway);
    }

}
