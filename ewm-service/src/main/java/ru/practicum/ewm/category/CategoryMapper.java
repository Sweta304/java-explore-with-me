package ru.practicum.ewm.category;


import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import ru.practicum.ewm.category.model.Category;

public class CategoryMapper {

    public static CategoryDto toCategoryDto(Category category) {
        return CategoryDto
                .builder()
                .id(category.getId())
                .name(category.getCategoryName())
                .build();
    }

    public static Category fromCategoryDto(CategoryDto categoryDto) {
        Category category = new Category();
        category.setCategoryName(categoryDto.getName());
        return category;
    }

    public static Category fromNewCategoryDto(NewCategoryDto newCategoryDto) {
        Category category = new Category();
        category.setCategoryName(newCategoryDto.getName());
        return category;
    }
}
