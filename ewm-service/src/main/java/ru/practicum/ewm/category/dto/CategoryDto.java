package ru.practicum.ewm.category.dto;

import lombok.*;
import lombok.extern.jackson.Jacksonized;

import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@Builder
@Jacksonized
public class CategoryDto {
    private Long id;
    private String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CategoryDto that = (CategoryDto) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
