package com.slavisa.BudgetApp.service;

import com.slavisa.BudgetApp.model.Category;
import com.slavisa.BudgetApp.repository.CategoryRepo;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepo categoryRepo;

    public List<Category> getAllCategories(){
        return categoryRepo.findAll();
    }

    public Optional<Category> getCategoryById(Integer categoryID){
        return categoryRepo.findById(categoryID);
    }

    public void deleteCategory(Integer categoryID){
        categoryRepo.deleteById(categoryID);
    }

    public Category updateCategory(Integer categoryID, Category category){
        Category category1 = categoryRepo.findById(categoryID).orElseThrow(() -> new RuntimeException("Category doesn't exist."));
        category1.setTitle(category.getTitle());
        return categoryRepo.save(category1);
    }

    public Category addCategory(@Valid Category category) {
        return categoryRepo.save(category);
    }
}
