package com.slavisa.budgetapp.service;

import com.slavisa.budgetapp.model.Account;
import com.slavisa.budgetapp.model.Role;
import com.slavisa.budgetapp.repository.AccountRepo;
import com.slavisa.budgetapp.repository.RoleRepo;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccountService {

    private final AccountRepo accountRepo;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepo roleRepo;

    public AccountService(AccountRepo accountRepo, PasswordEncoder passwordEncoder, RoleRepo roleRepo) {
        this.accountRepo = accountRepo;
        this.passwordEncoder = passwordEncoder;
        this.roleRepo = roleRepo;
    }

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
