package com.slavisa.budgetapp.controller;

import com.slavisa.budgetapp.model.Category;
import com.slavisa.budgetapp.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryController categoryController;

    private Category testCategory;

    @BeforeEach
    void setUp() {
        testCategory = new Category();
        testCategory.setCategoryID(1);
        testCategory.setTitle("Food");
    }

    @Test
    void testGetAllCategories() {
        Category transport = new Category();
        transport.setCategoryID(2);
        transport.setTitle("Transport");

        when(categoryService.getAllCategories()).thenReturn(List.of(testCategory, transport));

        List<Category> result = categoryController.getAllCategories();

        assertEquals(2, result.size());
        assertEquals("Food", result.get(0).getTitle());
        assertEquals("Transport", result.get(1).getTitle());
        verify(categoryService).getAllCategories();
    }

    @Test
    void testGetAllCategoriesEmptyList() {
        when(categoryService.getAllCategories()).thenReturn(List.of());

        List<Category> result = categoryController.getAllCategories();

        assertTrue(result.isEmpty());
        verify(categoryService).getAllCategories();
    }

    @Test
    void testSuccessfullyGetCategoryById() {
        when(categoryService.getCategoryById(1)).thenReturn(Optional.of(testCategory));

        Optional<Category> result = categoryController.getCategoryByID(1);

        assertTrue(result.isPresent());
        assertEquals("Food", result.get().getTitle());
        assertEquals(1, result.get().getCategoryID());
        verify(categoryService).getCategoryById(1);
    }

    @Test
    void testFailedGetCategoryById() {
        when(categoryService.getCategoryById(999)).thenReturn(Optional.empty());

        Optional<Category> result = categoryController.getCategoryByID(999);

        assertFalse(result.isPresent());
        verify(categoryService).getCategoryById(999);
    }

    @Test
    void testDeleteCategoryByID() {
        categoryController.deleteCategoryByID(1);

        verify(categoryService).deleteCategory(1);
    }

    @Test
    void testAddCategory() {
        Category newCategory = new Category();
        newCategory.setTitle("Bills");

        Category savedCategory = new Category();
        savedCategory.setCategoryID(3);
        savedCategory.setTitle("Bills");

        when(categoryService.addCategory(any(Category.class))).thenReturn(savedCategory);

        Category result = categoryController.addCategory(newCategory);

        assertEquals(3, result.getCategoryID());
        assertEquals("Bills", result.getTitle());
        verify(categoryService).addCategory(newCategory);
    }

    @Test
    void testUpdateCategory() {
        Category updateCategory = new Category();
        updateCategory.setTitle("Groceries");

        Category updatedCategory = new Category();
        updatedCategory.setCategoryID(1);
        updatedCategory.setTitle("Groceries");

        when(categoryService.updateCategory(anyInt(), any(Category.class))).thenReturn(updatedCategory);

        Category result = categoryController.updateCategory(1, updateCategory);

        assertEquals(1, result.getCategoryID());
        assertEquals("Groceries", result.getTitle());
        verify(categoryService).updateCategory(1, updateCategory);
    }
}
