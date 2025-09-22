package com.slavisa.BudgetApp.controller;

import com.slavisa.BudgetApp.model.Category;
import com.slavisa.BudgetApp.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

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
