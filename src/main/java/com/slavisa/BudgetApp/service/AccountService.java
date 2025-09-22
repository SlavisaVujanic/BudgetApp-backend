package com.slavisa.BudgetApp.service;

import com.slavisa.BudgetApp.model.Account;
import com.slavisa.BudgetApp.model.Role;
import com.slavisa.BudgetApp.repository.AccountRepo;
import com.slavisa.BudgetApp.repository.RoleRepo;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccountService {

    @Autowired
    private AccountRepo accountRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepo roleRepo;

    public List<Account> getAllAccounts(){
        return accountRepo.findAll();
    }

    public Optional<Account> getAccountById(Integer accountID){
        return accountRepo.findById(accountID);
    }

    public void deleteAccount(Integer accountID){
        accountRepo.deleteById(accountID);
    }

    public Account updateAccount(Integer accountID, Account account){
        Account account1 = accountRepo.findById(accountID)
                .orElseThrow(() -> new RuntimeException("Account doesn't exist."));

        account1.setFirstName(account.getFirstName());
        account1.setLastName(account.getLastName());
        account1.setEmail(account.getEmail());

        if (account.getPassword() != null && !account.getPassword().isEmpty()) {
            account1.setPassword(passwordEncoder.encode(account.getPassword()));
        }
        
        if (account.getRole() != null && account.getRole().getRoleID() != null) {
            Role role = roleRepo.findById(account.getRole().getRoleID())
                    .orElseThrow(() -> new RuntimeException("Role not found"));
            account1.setRole(role);
        }

        return accountRepo.save(account1);
    }

    public Account addAccount(@Valid Account account){
        if (account.getRole() != null && account.getRole().getRoleID() != null) {
            Role role = roleRepo.findById(account.getRole().getRoleID())
                    .orElseThrow(() -> new RuntimeException("Role not found"));
            account.setRole(role);
        }
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        return accountRepo.save(account);
    }
}
