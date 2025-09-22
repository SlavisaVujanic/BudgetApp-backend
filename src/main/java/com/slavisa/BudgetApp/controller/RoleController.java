package com.slavisa.BudgetApp.controller;

import com.slavisa.BudgetApp.model.Role;
import com.slavisa.BudgetApp.service.RoleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @GetMapping
    public List<Role> getAllRoles(){
        return roleService.getAllRoles();
    }

    @GetMapping("/{roleID}")
    public Optional<Role> getRoleByID(@PathVariable Integer roleID){
        return roleService.getRoleById(roleID);
    }

    @DeleteMapping("/delete/{roleID}")
    public void deleteRoleByID(@PathVariable Integer roleID){
        roleService.deleteRoleByID(roleID);
    }

    @PostMapping("/add")
    public Role addRole(@RequestBody @Valid Role role){
        return roleService.addRole(role);
    }

    @PutMapping("/update/{roleID}")
    public Role updateRole(@PathVariable Integer roleID, @RequestBody @Valid Role role){
        return roleService.updateRole(roleID,role);
    }
}
