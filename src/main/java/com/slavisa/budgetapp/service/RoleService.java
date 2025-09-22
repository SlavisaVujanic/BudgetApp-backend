package com.slavisa.budgetapp.service;

import com.slavisa.budgetapp.model.Role;
import com.slavisa.budgetapp.repository.RoleRepo;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoleService {

    private final RoleRepo roleRepo;

    public RoleService(RoleRepo roleRepo) {
        this.roleRepo = roleRepo;
    }

    public List<Role> getAllRoles(){
        return roleRepo.findAll();
    }

    public Optional<Role> getRoleById(Integer roleID){
        return roleRepo.findById(roleID);
    }

    public void deleteRoleByID(Integer roleID){
        roleRepo.deleteById(roleID);
    }

    public Role updateRole(Integer roleID, Role role){
        Role role1 = roleRepo.findById(roleID).orElseThrow(() -> new RuntimeException("Role doesn't exist."));
        role1.setRoleName(role.getRoleName());
        return roleRepo.save(role1);
    }

    public Role addRole(@Valid Role role) {
        return roleRepo.save(role);
    }
}
