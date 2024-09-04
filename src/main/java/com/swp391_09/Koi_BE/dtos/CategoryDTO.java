package com.swp391_09.Koi_BE.dtos;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class CategoryDTO {

    @NotEmpty(message = "Category name is required")
    private String name;

}