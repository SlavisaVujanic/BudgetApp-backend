package com.slavisa.BudgetApp.controller;

import com.slavisa.BudgetApp.model.Account;
import com.slavisa.BudgetApp.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @GetMapping
    public List<Account> getAllAccounts(){
        return accountService.getAllAccounts();
    }

    @GetMapping("/{accountID}")
    public Optional<Account> getAccountByID(@PathVariable Integer accountID){
        return accountService.getAccountById(accountID);
    }

    @DeleteMapping("/delete/{accountID}")
    public void deleteAccountByID(@PathVariable Integer accountID){
        accountService.deleteAccount(accountID);
    }

    @PostMapping("/add")
    public  Account addAccount(@RequestBody @Valid Account account){
        return accountService.addAccount(account);
    }

    @PutMapping("/update/{accountID}")
    public Account updateAccount(@PathVariable Integer accountID, @RequestBody @Valid Account account){
        return accountService.updateAccount(accountID,account);
    }
}
