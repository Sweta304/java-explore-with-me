package ru.practicum.ewm.category.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.category.CategoryMapper;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryJpaRepository;
import ru.practicum.ewm.event.repository.EventJpaRepository;
import ru.practicum.ewm.exceptions.CategoryNotFoundException;
import ru.practicum.ewm.exceptions.ConflictException;
import ru.practicum.ewm.utils.MyPageable;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.ewm.category.CategoryMapper.fromNewCategoryDto;
import static ru.practicum.ewm.category.CategoryMapper.toCategoryDto;
import static ru.practicum.ewm.utils.Constants.SORT_BY_ID;


@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryJpaRepository categoryJpaRepository;
    private final EventJpaRepository eventJpaRepository;

    @Autowired
    public CategoryServiceImpl(CategoryJpaRepository categoryJpaRepository, EventJpaRepository eventJpaRepository) {
        this.categoryJpaRepository = categoryJpaRepository;
        this.eventJpaRepository = eventJpaRepository;
    }

    @Override
    public CategoryDto changeCategory(CategoryDto categoryDto) throws CategoryNotFoundException, ConflictException {
        Long id = categoryDto.getId();
        String name = categoryDto.getName();
        Category category = categoryJpaRepository.findById(id).orElseThrow(() -> new CategoryNotFoundException("Категории с id " + id + " не существует", "Категория не найдена в таблице"));
        if (!category.getCategoryName().equals(name)) {
            checkUniqueCategoryName(name);
            category.setCategoryName(name);
        }
        log.info("Имя категории {} изменено на {}", id, name);
        return toCategoryDto(categoryJpaRepository.save(category));
    }

    @Override
    public CategoryDto addCategory(NewCategoryDto newCategoryDto) throws ConflictException {
        Category category = fromNewCategoryDto(newCategoryDto);
        checkUniqueCategoryName(newCategoryDto.getName());
        category = categoryJpaRepository.save(category);
        return toCategoryDto(category);
    }

    @Override
    public void deleteCategory(Long id) throws CategoryNotFoundException, ConflictException {
        Category category = categoryJpaRepository.findById(id).orElseThrow(() -> new CategoryNotFoundException("Категории с id " + id + " не существует", "Категория не найдена в таблице"));
        if (!
                eventJpaRepository.existsByCategoryId(id)) {
            categoryJpaRepository.delete(category);
        } else {
            throw new ConflictException("Категория не может быть удалена", "Для данной категории существуют созданные события");
        }
    }

    @Override
    public List<CategoryDto> findAll(Integer from, Integer size) {
        Pageable page = new MyPageable(from, size, SORT_BY_ID);
        List<Category> categories = categoryJpaRepository.findAll(page).getContent();
        return categories.stream()
                .map(CategoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto findById(Long id) throws CategoryNotFoundException {
        Category category = categoryJpaRepository.findById(id).orElseThrow(() -> new CategoryNotFoundException("Категория не найдена", "Категории с id " + id + "не существует"));
        return toCategoryDto(category);
    }

    private void checkUniqueCategoryName(String name) throws ConflictException {
        if (categoryJpaRepository.findByCategoryName(name) != null) {
            throw new ConflictException("Некорректное имя категории", "Категория с таким именем уже существует");
        }
    }

}
