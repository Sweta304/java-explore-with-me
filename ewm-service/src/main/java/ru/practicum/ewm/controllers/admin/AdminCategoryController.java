package ru.practicum.ewm.controllers.admin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import ru.practicum.ewm.category.service.CategoryService;
import ru.practicum.ewm.exceptions.CategoryNotFoundException;
import ru.practicum.ewm.exceptions.ConflictException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/admin/categories")
public class AdminCategoryController {
    private final CategoryService categoryService;

    @Autowired
    public AdminCategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public CategoryDto addCategory(@RequestBody @Valid NewCategoryDto newCategoryDto) throws ConflictException {
        log.info("Админ создает новую категорию {}", newCategoryDto.getName());
        return categoryService.addCategory(newCategoryDto);
    }

    @PatchMapping
    public CategoryDto changeCategory(@RequestBody @Valid CategoryDto categoryDto) throws CategoryNotFoundException, ConflictException {
        log.info("Админ редактирует категорию с id {}", categoryDto.getId());
        return categoryService.changeCategory(categoryDto);
    }

    @DeleteMapping("/{catId}")
    public void deleteCategory(@PathVariable @Positive Long catId) throws CategoryNotFoundException, ConflictException {
        categoryService.deleteCategory(catId);
    }
}
