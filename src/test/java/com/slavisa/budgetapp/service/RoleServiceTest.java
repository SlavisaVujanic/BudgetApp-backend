package com.slavisa.budgetapp.service;

import com.slavisa.budgetapp.model.Role;
import com.slavisa.budgetapp.repository.RoleRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock
    private RoleRepo roleRepo;

    @InjectMocks
    private RoleService roleService;

    @Test
    void testGetAllRoles() {
        Role r1 = new Role();
        r1.setRoleName("Admin");
        r1.setRoleID(1);
        Role r2 = new Role();
        r2.setRoleName("User");
        r2.setRoleID(2);

        when(roleRepo.findAll()).thenReturn(Arrays.asList(r1,r2));

        List<Role> roles = roleService.getAllRoles();

        assertEquals(2,roles.size());
        verify(roleRepo).findAll();
    }

    @Test
    void testSuccessfullyGetRoleById() {
        Role role = new Role();
        role.setRoleName("User");
        role.setRoleID(1);

        when(roleRepo.findById(role.getRoleID())).thenReturn(Optional.of(role));

        Optional<Role> foundRole = roleService.getRoleById(1);

        assertTrue(foundRole.isPresent());
        assertEquals("User",foundRole.get().getRoleName());
    }

    @Test
    void testFailedGetRoleById() {
        int roleID = 99;

        when(roleRepo.findById(roleID)).thenReturn(Optional.empty());

        Optional<Role> foundRole = roleService.getRoleById(roleID);

        assertTrue(foundRole.isEmpty());
    }

    @Test
    void testDeleteRoleByID() {
        Role role = new Role();
        role.setRoleName("Admin");
        role.setRoleID(1);

        roleService.deleteRoleByID(1);

        verify(roleRepo,Mockito.times(1)).deleteById(1);
    }

    @Test
    void testSuccessfullyUpdateRole() {
        int roleID = 1;
        Role existingRole = new Role();
        existingRole.setRoleID(roleID);
        existingRole.setRoleName("User");

        Role updatedRole = new Role();
        updatedRole.setRoleName("Admin");

        when(roleRepo.findById(roleID)).thenReturn(Optional.of(existingRole));
        when(roleRepo.save(existingRole)).thenReturn(existingRole);

        Role result = roleService.updateRole(roleID,updatedRole);

        assertEquals("Admin",result.getRoleName());
        verify(roleRepo).findById(roleID);
        verify(roleRepo).save(existingRole);
    }

    @Test
    void testFailedUpdateRole() {
        int roleID = 1;
        Role updatedRole = new Role();
        updatedRole.setRoleName("Admin");

        when(roleRepo.findById(roleID)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> roleService.updateRole(roleID,updatedRole));

        assertEquals("Role doesn't exist.", ex.getMessage());
        verify(roleRepo).findById(roleID);
        verify(roleRepo,Mockito.never()).save(Mockito.any());
    }

    @Test
    void testAddRole() {
        Role newRole = new Role();
        newRole.setRoleName("Manager");

        Role savedRole = new Role();
        savedRole.setRoleID(1);
        savedRole.setRoleName("Manager");

        when(roleRepo.save(newRole)).thenReturn(savedRole);

        Role result = roleService.addRole(newRole);

        assertNotNull(result.getRoleID());
        assertEquals("Manager",result.getRoleName());
    }
}