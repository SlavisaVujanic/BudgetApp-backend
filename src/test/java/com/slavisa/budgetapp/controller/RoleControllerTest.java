package com.slavisa.budgetapp.controller;

import com.slavisa.budgetapp.model.Role;
import com.slavisa.budgetapp.service.RoleService;
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
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RoleControllerTest {

    @Mock
    private RoleService roleService;

    @InjectMocks
    private RoleController roleController;

    private Role testRole;

    @BeforeEach
    void setUp() {
        testRole = new Role();
        testRole.setRoleID(1);
        testRole.setRoleName("ADMIN");
    }

    @Test
    void testGetAllRoles() {
        Role userRole = new Role();
        userRole.setRoleID(2);
        userRole.setRoleName("USER");

        when(roleService.getAllRoles()).thenReturn(List.of(testRole, userRole));

        List<Role> result = roleController.getAllRoles();

        assertEquals(2, result.size());
        assertEquals("ADMIN", result.get(0).getRoleName());
        assertEquals("USER", result.get(1).getRoleName());
        verify(roleService).getAllRoles();
    }

    @Test
    void testGetAllRolesEmptyList() {
        when(roleService.getAllRoles()).thenReturn(List.of());

        List<Role> result = roleController.getAllRoles();

        assertTrue(result.isEmpty());
        verify(roleService).getAllRoles();
    }

    @Test
    void testSuccessfullyGetRoleByID() {
        when(roleService.getRoleById(1)).thenReturn(Optional.of(testRole));

        Optional<Role> result = roleController.getRoleByID(1);

        assertTrue(result.isPresent());
        assertEquals("ADMIN", result.get().getRoleName());
        assertEquals(1, result.get().getRoleID());
        verify(roleService).getRoleById(1);
    }

    @Test
    void testFailedGetRoleByID() {
        when(roleService.getRoleById(999)).thenReturn(Optional.empty());

        Optional<Role> result = roleController.getRoleByID(999);

        assertFalse(result.isPresent());
        verify(roleService).getRoleById(999);
    }

    @Test
    void testDeleteRoleByID() {
        roleController.deleteRoleByID(1);

        verify(roleService).deleteRoleByID(1);
    }

    @Test
    void testAddRole() {
        Role newRole = new Role();
        newRole.setRoleName("MODERATOR");

        Role savedRole = new Role();
        savedRole.setRoleID(3);
        savedRole.setRoleName("MODERATOR");

        when(roleService.addRole(any(Role.class))).thenReturn(savedRole);

        Role result = roleController.addRole(newRole);

        assertEquals(3, result.getRoleID());
        assertEquals("MODERATOR", result.getRoleName());
        verify(roleService).addRole(newRole);
    }

    @Test
    void testUpdateRole() {
        Role updateRole = new Role();
        updateRole.setRoleName("SUPER_ADMIN");

        Role updatedRole = new Role();
        updatedRole.setRoleID(1);
        updatedRole.setRoleName("SUPER_ADMIN");

        when(roleService.updateRole(anyInt(), any(Role.class))).thenReturn(updatedRole);

        Role result = roleController.updateRole(1, updateRole);

        assertEquals(1, result.getRoleID());
        assertEquals("SUPER_ADMIN", result.getRoleName());
        verify(roleService).updateRole(1, updateRole);
    }
}