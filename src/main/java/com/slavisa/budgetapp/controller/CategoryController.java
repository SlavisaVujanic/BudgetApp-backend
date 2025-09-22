package com.slavisa.budgetapp.controller;

import com.slavisa.budgetapp.model.Category;
import com.slavisa.budgetapp.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/category")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public List<Category> getAllCategories(){
        return categoryService.getAllCategories();
    }

    @GetMapping("/{categoryID}")
    public Optional<Category> getCategoryByID(@PathVariable Integer categoryID){
        return categoryService.getCategoryById(categoryID);
    }

    @DeleteMapping("/delete/{categoryID}")
    public void deleteCategoryByID(@PathVariable Integer categoryID){
        categoryService.deleteCategory(categoryID);
    }

    @PostMapping("/add")
    public Category addCategory(@RequestBody @Valid Category category){
        return categoryService.addCategory(category);
    }

    @PutMapping("/update/{categoryID}")
    public Category updateCategory(@PathVariable Integer categoryID, @RequestBody @Valid Category category){
        return categoryService.updateCategory(categoryID,category);
    }

}
