package com.slavisa.budgetapp.service;

import com.slavisa.budgetapp.model.Category;
import com.slavisa.budgetapp.repository.CategoryRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepo categoryRepo;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void testGetAllCategories() {
        Category category = new Category();
        category.setTitle("Salary");

        Category category1 = new Category();
        category1.setTitle("Bills");

        when(categoryRepo.findAll()).thenReturn(Arrays.asList(category,category1));

        List<Category> categories = categoryService.getAllCategories();

        assertEquals(2,categories.size());
        verify(categoryRepo).findAll();
    }

    @Test
    void testSuccessfullyGetCategoryById() {
        Category category = new Category();
        category.setTitle("Bills");
        category.setCategoryID(32);

        when(categoryRepo.findById(category.getCategoryID())).thenReturn(Optional.of(category));

        Optional<Category> cat = categoryService.getCategoryById(32);

        assertTrue(cat.isPresent());
        assertEquals("Bills",cat.get().getTitle());
    }

    @Test
    void testFailedGetCategoryById() {
        int categoryID = 2;

        when(categoryRepo.findById(categoryID)).thenReturn(Optional.empty());

        Optional<Category> foundCategory = categoryService.getCategoryById(categoryID);

        assertTrue(foundCategory.isEmpty());
    }

    @Test
    void testDeleteCategory() {
        Category category = new Category();
        category.setCategoryID(12);
        category.setTitle("Car");

        categoryService.deleteCategory(12);

        verify(categoryRepo, times(1)).deleteById(12);
    }

    @Test
    void testSuccessfullyUpdateCategory() {
        int categoryID = 7;
        Category existing = new Category();
        existing.setCategoryID(categoryID);
        existing.setTitle("Car");

        Category updated = new Category();
        updated.setTitle("Garden");

        when(categoryRepo.findById(categoryID)).thenReturn(Optional.of(existing));
        when(categoryRepo.save(existing)).thenReturn(existing);

        Category result = categoryService.updateCategory(categoryID,updated);

        assertEquals("Garden", result.getTitle());
        verify(categoryRepo).findById(categoryID);
        verify(categoryRepo).save(existing);
    }

    @Test
    void testFailedUpdateCategory(){
        int categoryID = 2;
        Category updated = new Category();
        updated.setTitle("Car");

        when(categoryRepo.findById(categoryID)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> categoryService.updateCategory(categoryID,updated));

        assertEquals("Category doesn't exist.", ex.getMessage());
        verify(categoryRepo).findById(categoryID);
        verify(categoryRepo, never()).save(any());
    }

    @Test
    void addCategory() {
        Category category = new Category();
        category.setTitle("House");

        Category saved = new Category();
        saved.setTitle("House");
        saved.setCategoryID(10);

        when(categoryRepo.save(category)).thenReturn(saved);

        Category result = categoryService.addCategory(category);

        assertNotNull(result.getCategoryID());
        assertEquals("House",result.getTitle());
    }

}