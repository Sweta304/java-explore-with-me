package ru.practicum.ewm.category.service;


import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import ru.practicum.ewm.exceptions.CategoryNotFoundException;
import ru.practicum.ewm.exceptions.ConflictException;

import java.util.List;


public interface CategoryService {

    CategoryDto changeCategory(CategoryDto categoryDto) throws CategoryNotFoundException, ConflictException;

    CategoryDto addCategory(NewCategoryDto newCategoryDto) throws ConflictException;

    void deleteCategory(Long id) throws CategoryNotFoundException, ConflictException;

    List<CategoryDto> findAll(Integer from, Integer size);

    CategoryDto findById(Long id) throws CategoryNotFoundException;
}
