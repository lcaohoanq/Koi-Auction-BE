package com.swp391.koibe.services.category;

import com.swp391.koibe.dtos.CategoryDTO;
import com.swp391.koibe.exceptions.base.DataAlreadyExistException;
import com.swp391.koibe.exceptions.base.DataNotFoundException;
import com.swp391.koibe.models.Category;
import com.swp391.koibe.responses.CategoryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ICategoryService {

    Category createCategory(CategoryDTO category) throws DataAlreadyExistException;
    Category getById(int id) throws DataNotFoundException;
    Page<CategoryResponse> getAllCategories(Pageable pageable);
    void update(int categoryId, CategoryDTO category) throws DataNotFoundException;
    void delete(int id);

}
