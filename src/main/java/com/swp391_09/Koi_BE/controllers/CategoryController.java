package com.swp391_09.Koi_BE.controllers;

import com.swp391_09.Koi_BE.models.Category;
import com.swp391_09.Koi_BE.repositories.CategoryRepository;
import com.swp391_09.Koi_BE.responses.CategoryResponse;
import com.swp391_09.Koi_BE.services.CategoryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.prefix}/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("")
    public ResponseEntity<?> getAllCategories() {
        try{
            return ResponseEntity.ok(categoryService.getAllCategories());
        }catch (Exception e){
            return ResponseEntity.badRequest().body("Error");
        }

    }

}
